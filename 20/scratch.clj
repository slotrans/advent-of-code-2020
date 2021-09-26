(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
    )
)





(defn to-bit-string [s] 
    (-> s 
        (str/replace ,,, "#" "1") 
        (str/replace ,,, "." "0")
    )
)

(defn tile-edge-to-number [s]
    (edn/read-string (str "2r" (to-bit-string s)))
)


(def all-tile-edges
    [ ["#...##.#..", ".#..#####.", "#.##...##.", "#..#..#.##"] 
    , ["..###..###", "#..##.#...", "..##.#..#.", ".#..#####."]
    , ["#.#.#####.", ".#....#...", "..#.###...", "#..##.#..."]
    , ["#.##...##.", "......#..#", "...#.#.#.#", "####....#."]
    , ["..##.#..#.", ".#.#.###..", "###.##.#..", "......#..#"]
    , ["..#.###...", ".####....#", ".##...####", ".#.#.###.."]
    , ["...#.#.#.#", "#.#.##...#", "..#.#....#", "...#..###."]
    , ["###.##.#..", ".#..#.....", "##.#.#....", "#.#.##...#"]
    , [".##...####", "###....##.", "..##......", ".#..#....."]
    ]
)

(doseq [tile all-tile-edges]
    (println 
        (for [edge tile]
            (tile-edge-to-number edge)
        )
    )
)

;; using the sample for a simplified version of the problem
;; IDs are as in the sample
;; Names are arbitrary, to be more differentiable than 4-digit numbers.
;; They were assigned in the order the tiles are listed in the sample input.
;; The edge values are computed by interpreting the #/. as 1/0, which gives
;; a 10-bit number for each edge. Top and Bottom edges are read left-to-right,
;; Left and Right edges are read top-to-bottom. The strings above were manually
;; transcribed from the instructions.
;;
;; Tiles are listed here in L2R,T2B solution order (which will not match the
;; sorted-map based order below... that will be B,H,G,A,D,E,I,F,C).
(def tile-data
    { "B" {:id 1951 :name "B" :top 564 :right 318 :bottom 710 :left 587}
    , "A" {:id 2311 :name "A" :top 231 :right 616 :bottom 210 :left 318}
    , "I" {:id 3079 :name "I" :top 702 :right 264 :bottom 184 :left 616}

    , "H" {:id 2729 :name "H" :top 710 :right   9 :bottom 85  :left 962}
    , "D" {:id 1427 :name "D" :top 210 :right 348 :bottom 948 :left   9}
    , "F" {:id 2473 :name "F" :top 184 :right 481 :bottom 399 :left 348}
    
    , "G" {:id 2971 :name "G" :top 85  :right 689 :bottom 161 :left  78}
    , "E" {:id 1489 :name "E" :top 948 :right 288 :bottom 848 :left 689}
    , "C" {:id 1171 :name "C" :top 399 :right 902 :bottom 192 :left 288}
    }
)


;; Using sorted-map with [x y] keys, and [0 0] defined as the upper-left corner,
;; results in a map whose order is top-to-bottom-then-left-to-right. That is,
;; iterating over the keys will go down the left column to the bottom, then move
;; to the top of the second column, and so on. This isn't how I naturally think of
;; things (I would go L->R first), but it's a natural consequence of putting the X
;; coordinate first. Since the order we move through the board doesn't matter,
;; we'll just accept it.
(def initial-board ; pairs are { [x y] tile-name }
    (into
        (sorted-map)
        { [0 0] nil
        , [0 1] nil
        , [0 2] nil

        , [1 0] nil
        , [1 1] nil
        , [1 2] nil
     
        , [2 0] nil
        , [2 1] nil
        , [2 2] nil
        }
    )
)


(def all-tile-names (into (sorted-set) (keys tile-data)))


(def initial-state
    { :board initial-board
    , :tile-names all-tile-names
    ; a solved true/false flag?
    }
)


(defn get-next-space [board] ; -> [x y], or nil if the board is complete
    ;; using a sorted-map makes this function easy
    (if-let [ next-space (first 
                             (filter 
                                 #(nil? (val %)) 
                                 board
                             )
                         )
            ]
        (key next-space)
        nil
    )
)


(defn shift-coords-in-direction [coords direction] ; -> coords
    (let [[x y] coords]
        (condp = direction
            :top    [   x    (- y 1)]
            :right  [(+ x 1)    y   ]
            :bottom [   x    (+ y 1)]
            :left   [(- x 1)    y   ]
        )
    )
)


(defn matches-tile-in-direction [tile-data board tile-name coords direction] ; -> true/false
    (let [ tile (get tile-data tile-name)
         , other-tile-coords (shift-coords-in-direction coords direction)
         , other-tile-name (get board other-tile-coords)
         , other-tile (get tile-data other-tile-name)
         , other-edge-key (direction {:top :bottom, :right :left, :bottom :top, :left :right})
         , other-edge (other-edge-key other-tile)
         ]
        (or (nil? other-edge) ; no tile in that direction
            (= other-edge (direction tile)) ; edges match
        )        
    )
)


(defn place-tile [tile-data state tile-name coords] ; -> state
    (let [ board (:board state)
         , tile-names (:tile-names state)
         ]
        (if (and (matches-tile-in-direction tile-data board tile-name coords :top)
                 (matches-tile-in-direction tile-data board tile-name coords :right)
                 (matches-tile-in-direction tile-data board tile-name coords :bottom)
                 (matches-tile-in-direction tile-data board tile-name coords :left)
            )
            ;; it fits! return a new state with this tile at the requested coordinates, and removed from the set of available tiles
            { :board (assoc board coords tile-name)
            , :tile-names (disj tile-names tile-name)
            }
            ;; it doesn't fit!
            nil
        )
    )
)


(defn solve [tile-data state] ;-> sequence of possible states
    (println state)
    (if-let [next-space (get-next-space (:board state))]
        ;; try to place each available tile in the next space, and recursively solve the resulting states
        (flatten
            (map
                #(solve tile-data %)
                (remove
                    nil?
                    (for [tile-name (:tile-names state)]
                        (place-tile tile-data state tile-name next-space)
                    )
                )
            )
        )
        ;; recursive base case: board is complete, return it
        [state]
    )
)
