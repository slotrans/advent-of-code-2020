(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
    )
)

;;; INPUT PARSING & TILE MANIPULATION FUNCTIONS ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn parse-tile-pixels [s] ; -> vector of lines, where each line is a vector of characters (pixels)
    (mapv
        vec
        (str/split-lines s)
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


(defn get-tile-edge [tile-data direction]
    (condp = direction
        :top    (apply str (get tile-data 0))
        :right  (apply str (for [line tile-data] (get line 9)))
        :bottom (apply str (get tile-data 9))
        :left   (apply str (for [line tile-data] (get line 0)))
    )
)


(defn get-edge-values [tile-pixels]
    { :top    (tile-edge-to-number (get-tile-edge tile-pixels :top))
    , :right  (tile-edge-to-number (get-tile-edge tile-pixels :right))
    , :bottom (tile-edge-to-number (get-tile-edge tile-pixels :bottom))
    , :left   (tile-edge-to-number (get-tile-edge tile-pixels :left))
    }
)


(defn rotate-tile [tile-pixels degrees] ; -> tile-pixels, rotated clockwise by degrees
    (let [size-of-tile (count (first tile-pixels))]
        (condp = degrees
              0 tile-pixels
             90 (vec
                    (for [pos (range 0 size-of-tile)] ; for each position, left to right
                        (mapv
                            #(get % pos)
                            (reverse tile-pixels) ; take from that position, moving bottom to top
                        )
                    )
                )
            180 (rotate-tile (rotate-tile tile-pixels 90) 90)
            270 (rotate-tile (rotate-tile (rotate-tile tile-pixels 90) 90) 90)
        )
    )
)


(defn mirror-tile-horiz [tile-pixels] ; -> tile-pixels, mirrored horizontally
    (mapv
        #(vec (reverse %))
        tile-pixels
    )
)


(defn mirror-tile-vert [tile-pixels] ; -> tile-pixels, mirrored vertically
    (mapv
        identity
        (reverse tile-pixels)
    )
)


(defn all-orientations [tile-pixels]
    {  0 (-> tile-pixels (rotate-tile ,,,   0))
    ,  1 (-> tile-pixels (rotate-tile ,,,  90))
    ,  2 (-> tile-pixels (rotate-tile ,,, 180))
    ,  3 (-> tile-pixels (rotate-tile ,,, 270))

    ,  4 (-> tile-pixels (mirror-tile-horiz ,,,) (rotate-tile ,,,   0))
    ,  5 (-> tile-pixels (mirror-tile-horiz ,,,) (rotate-tile ,,,  90))
    ,  6 (-> tile-pixels (mirror-tile-horiz ,,,) (rotate-tile ,,, 180))
    ,  7 (-> tile-pixels (mirror-tile-horiz ,,,) (rotate-tile ,,, 270))

    ;,  8 (-> tile-pixels (mirror-tile-vert ,,,) (rotate-tile ,,,   0))  same as 6
    ;,  9 (-> tile-pixels (mirror-tile-vert ,,,) (rotate-tile ,,,  90))  same as 7
    ;, 10 (-> tile-pixels (mirror-tile-vert ,,,) (rotate-tile ,,, 180))  same as 4
    ;, 11 (-> tile-pixels (mirror-tile-vert ,,,) (rotate-tile ,,, 270))  same as 5

    ;, 12 (-> tile-pixels (mirror-tile-horiz ,,,) (mirror-tile-vert ,,,) (rotate-tile ,,,   0))  same as 2
    ;, 13 (-> tile-pixels (mirror-tile-horiz ,,,) (mirror-tile-vert ,,,) (rotate-tile ,,,  90))  same as 3
    ;, 14 (-> tile-pixels (mirror-tile-horiz ,,,) (mirror-tile-vert ,,,) (rotate-tile ,,, 180))  same as 0
    ;, 15 (-> tile-pixels (mirror-tile-horiz ,,,) (mirror-tile-vert ,,,) (rotate-tile ,,, 270))  same as 1
    }
)


(defn enriched-tile [tile-id tile-pixels]
    ;(println (str "enriching tile " tile-id))
    { tile-id 
        (into
            (sorted-map) ; not _necessary_ but makes inspecting data easier
            (for [[orientation pixels] (all-orientations tile-pixels)]
                { orientation
                    (merge
                        { :id tile-id :orientation orientation :pixels pixels }
                        (get-edge-values pixels)
                    )
                }
            )
        )
    }
)


(defn parse-tile [s] ; -> map of tile-id -> map of orientation -> rich tile data
    (let [ [tile-name raw-tile-pixels] (str/split s #":\n")
         , [_ tile-id-as-str] (str/split tile-name #" ")
         , tile-id (Integer/parseInt tile-id-as-str)
         ]
        ;{tile-id (parse-tile-pixels raw-tile-pixels)}
        (let [tile-pixels (parse-tile-pixels raw-tile-pixels)]
            (enriched-tile tile-id tile-pixels)
        )
    )
)


(defn parse-input [s] ;-> map of tile-id -> pixels (across all tiles)
    (into
        {}
        (for [chunk (str/split s #"\n\n") :when (str/starts-with? chunk "Tile")]
            (parse-tile chunk)
        )
    )
)


;;; tile database ends up like...
; {
;   1951 {
;          0 { :id 1951 :orientation 0 :pixels [[...]] :top ... :right ... :bottom ... :left ... }
;          1 { :id 1951 :orientation 1 :pixels [[...]] :top ... :right ... :bottom ... :left ... }
;          2 { :id 1951 :orientation 2 :pixels [[...]] :top ... :right ... :bottom ... :left ... }
;          ...
;        }
;   2311 {
;          0 { :id 1951 :orientation 0 :pixels [[...]] :top ... :right ... :bottom ... :left ... }
;          1 { :id 1951 :orientation 1 :pixels [[...]] :top ... :right ... :bottom ... :left ... }
;          2 { :id 1951 :orientation 2 :pixels [[...]] :top ... :right ... :bottom ... :left ... }
;          ...
;        }
; }


;; this is a bit under-specified for tiles-with-orientation...
(defn print-tile [tile]
    (let [[id tile-data] tile]
        (println (str "id=" id))
        (doseq [line tile-data]
            (println (apply str line))
        )
    )
)


;;; SOLVING FUNCTIONS ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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


(defn matches-tile-in-direction [tile-data board tile-id orientation coords direction] ; -> true/false
    (let [ tile (get-in tile-data [tile-id orientation])
         , other-tile-coords (shift-coords-in-direction coords direction)
         , {other-tile-id :id other-tile-orientation :orientation} (get board other-tile-coords)
         , other-tile (get-in tile-data [other-tile-id other-tile-orientation])
         , other-edge-key (direction {:top :bottom, :right :left, :bottom :top, :left :right})
         , other-edge (other-edge-key other-tile)
         ]
        (or (nil? other-edge) ; no tile in that direction
            (= other-edge (direction tile)) ; edges match
        )        
    )
)


(defn place-tile [tile-data state tile-id orientation coords] ; -> state
    (let [ board (:board state)
         , tile-ids (:tile-ids state)
         ]
        (if (and (matches-tile-in-direction tile-data board tile-id orientation coords :top)
                 (matches-tile-in-direction tile-data board tile-id orientation coords :right)
                 (matches-tile-in-direction tile-data board tile-id orientation coords :bottom)
                 (matches-tile-in-direction tile-data board tile-id orientation coords :left)
            )
            ;; it fits! return a new state with this tile at the requested coordinates, and removed from the set of available tiles
            { :board (assoc board coords {:id tile-id :orientation orientation})
            , :tile-ids (disj tile-ids tile-id)
            }
            ;; it doesn't fit!
            nil
        )
    )
)


(defn solve [tile-data state] ;-> sequence of possible states
    ;(println state)
    (if-let [next-space (get-next-space (:board state))]
        ;; try to place each available tile in the next space, and recursively solve the resulting states
        (flatten
            (map
                #(solve tile-data %)
                (remove
                    nil?
                    (for [ tile-id (:tile-ids state)
                         , orientation (range 0 8)
                         ]
                        (place-tile tile-data state tile-id orientation next-space)
                    )
                )
            )
        )
        ;; recursive base case: board is complete, return it
        (do
            (println (str "found solution: " state))
            [state]
        )
    )
)




(def sample-input (slurp "sample-input.txt"))
(def parsed-sample-input (parse-input sample-input))
;(println parsed-sample-input)
(doseq [ tile parsed-sample-input
       , orientation (val tile)
       ]
    (println (select-keys (val orientation) [:id :orientation :top :right :bottom :left]))
)


;(def initial-board
;    (into
;        (sorted-map)
;        (for [ x (range 0 3)
;             , y (range 0 3)
;             ]
;            {[x y] nil}
;        )
;    )
;)
; the above unrolls to...

(def initial-board-sample ; pairs are { [x y] {:id NNNN :orientation N} }
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

(def sample-tile-ids (into (sorted-set) (keys parsed-sample-input)))

(def initial-state-sample
    { :board initial-board-sample
    , :tile-ids sample-tile-ids
    }
)

(def sample-solution (first (solve parsed-sample-input initial-state-sample)))
(let [ board (:board sample-solution)
     , corners [(get-in board [[0 0] :id]) 
                (get-in board [[0 2] :id]) 
                (get-in board [[2 0] :id]) 
                (get-in board [[2 2] :id])]
     ]
    (println "SAMPLE SOLUTION")
    (println (str "corner IDs: " corners))
    (println (str "product of corner IDs: " (apply * corners)))
)



(def initial-board-p1
    (into
        (sorted-map)
        (for [ x (range 0 12)
             , y (range 0 12)
             ]
            {[x y] nil}
        )
    )
)
(def input20 (slurp "input20.txt"))
(def parsed-input20 (parse-input input20))
(def input20-tile-ids (into (sorted-set) (keys parsed-input20)))
(def initial-state-p1
    { :board initial-board-p1
    , :tile-ids input20-tile-ids
    }
)

(def p1-solution (first (solve parsed-input20 initial-state-p1))) ; first-ing a lazy sequence doesn't early-exit as well as `reduced` but I can't see how to refactor `solve` and/or `place-tile` to a reducer
(let [ board (:board p1-solution)
     , corners [(get-in board [[0 0] :id]) 
                (get-in board [[0 11] :id]) 
                (get-in board [[11 0] :id]) 
                (get-in board [[11 11] :id])]
     ]
    (println "P1 SOLUTION")
    (println (str "corner IDs: " corners)) ; [1453 1459 3181 2543]
    (println (str "product of corner IDs: " (apply * corners))) ; 17148689442341
)


;;; FUNCTIONS FOR P2 ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn strip-borders [tile-pixels] ; -> pixel grid (vector of vectors)
    (mapv 
        #(vec (butlast (rest %)))
        (butlast (rest tile-pixels))
    )
)


(defn allocate-blank-grid [size] ; -> size X size pixel grid filled with _ characters
    (let [line (vec (repeat size \_))]
        (vec (repeat size line))
    )
)


(defn replace-references-with-stripped-tile-pixels [tile-data board] ; -> map of coords to pixel grids
    (into
        (sorted-map) ; not strictly necessary
        (for [board-space board]
            (let [ coords (key board-space)
                 , {:keys [id orientation]} (val board-space)
                 , tile-pixels (get-in tile-data [id orientation :pixels])
                 ]
                {coords (strip-borders tile-pixels)}
            )
        )
    )
)


(defn blit [grid tile-coords-pixels-pair] ; -> grid
    (let [ coords (key tile-coords-pixels-pair)
         , pixels (val tile-coords-pixels-pair)
         , [x y] coords ; coordinates in the tile grid
         , tile-size (count (first pixels))
         , dest-x-base (* x tile-size)
         , dest-y-base (* y tile-size)
         ]
        (reduce 
            (fn [grid_ [x y]]
                (assoc-in
                    grid_
                    [(+ dest-y-base y) (+ dest-x-base x)] ; vec-of-vec coords are y-first
                    (get-in pixels [y x])                 ; vec-of-vec coords are y-first
                )
            )
            grid
            (for [x (range 0 tile-size), y (range 0 tile-size)] [x y])
        )
    )
)


(defn full-picture [tile-data board] ; -> pixel grid (vector of vectors)
    (reduce
        blit
        (allocate-blank-grid (* 8 12)) ; tiles are 8x8 after stripping borders, grid is 12x12 tiles
        (replace-references-with-stripped-tile-pixels tile-data board)
    )
)


(def p2-picture (full-picture parsed-input20 (:board p1-solution)))
(doseq [line p2-picture]
    (println (apply str line))
)


;; using _ to denote a "this can be anything" space
;; note: 20 wide * 3 high
(def sea-monster
    [ (vec "__________________#_")
    , (vec "#____##____##____###")
    , (vec "_#__#__#__#__#__#___")
    ]
)
(def monster-height (count sea-monster))
(def monster-width (count (first sea-monster)))


(defn picture-region [picture x y width height]
    ;(println (str "x=" x ", y=" y ", width=" width ", height=" height))
    (mapv
        #(subvec % x (+ x width))
        (subvec picture y (+ y height))
    )
)


(defn monster? [picture]
    (every?
        true?
        (for [ y (range 0 monster-height)
             , x (range 0 monster-width)
             ]
            (or (= \_ (get-in sea-monster [y x]))
                (and (= \# (get-in sea-monster [y x]))
                     (= \# (get-in picture [y x]))
                )
            )
        )
    )
)


(defn write-monster [picture x y] ; -> picture, with \O written in the shape of the sea monster
    (-> picture
        ; first line
        (assoc-in ,,, [y (+ x 18)] \O)
        ; second line
        (assoc-in ,,, [(+ y 1) x] \O)
        (assoc-in ,,, [(+ y 1) (+ x 5)] \O)
        (assoc-in ,,, [(+ y 1) (+ x 6)] \O)
        (assoc-in ,,, [(+ y 1) (+ x 11)] \O)
        (assoc-in ,,, [(+ y 1) (+ x 12)] \O)
        (assoc-in ,,, [(+ y 1) (+ x 17)] \O)
        (assoc-in ,,, [(+ y 1) (+ x 18)] \O)
        (assoc-in ,,, [(+ y 1) (+ x 19)] \O)
        ; third line
        (assoc-in ,,, [(+ y 2) (+ x 1)] \O)
        (assoc-in ,,, [(+ y 2) (+ x 4)] \O)
        (assoc-in ,,, [(+ y 2) (+ x 7)] \O)
        (assoc-in ,,, [(+ y 2) (+ x 10)] \O)
        (assoc-in ,,, [(+ y 2) (+ x 13)] \O)
        (assoc-in ,,, [(+ y 2) (+ x 16)] \O)
    )
)


(defn find-monsters [picture]
    (let [ picture-height (count picture)
         , picture-width (count (first picture))
         ]
        (doseq [ y (range 0 (- picture-height monster-height))
               , x (range 0 (- picture-width monster-width))
               ]
            (if (monster? (picture-region picture x y monster-width monster-height))
                (println (str "sea monster found at (" x ", " y ")"))
            )
        )
    )
)


(defn find-monsters-and-replace [picture]
    (let [ picture-height (count picture)
         , picture-width (count (first picture))
         , coords-to-check (for [ y (range 0 (- picture-height monster-height))
                                , x (range 0 (- picture-width monster-width))
                                ]
                                [x y]
                           )
         ]
        (println coords-to-check)
        (loop [ modified-picture picture
              , remaining-coords coords-to-check
              ]
            (if-let [[x y] (first remaining-coords)]
                (if (monster? (picture-region picture x y monster-width monster-height))
                    (do
                        (println (str "sea monster found at (" x ", " y ")"))
                        (recur (write-monster modified-picture x y) (rest remaining-coords))
                    )
                    (recur modified-picture (rest remaining-coords))
                )
                modified-picture
            )
        )
    )
)



(def p2-sample-picture
    [ (vec ".####...#####..#...###..")
    , (vec "#####..#..#.#.####..#.#.")
    , (vec ".#.#...#.###...#.##.##..")
    , (vec "#.#.##.###.#.##.##.#####")
    , (vec "..##.###.####..#.####.##")
    , (vec "...#.#..##.##...#..#..##")
    , (vec "#.##.#..#.#..#..##.#.#..")
    , (vec ".###.##.....#...###.#...")
    , (vec "#.####.#.#....##.#..#.#.")
    , (vec "##...#..#....#..#...####")
    , (vec "..#.##...###..#.#####..#")
    , (vec "....#.##.#.#####....#...")
    , (vec "..##.##.###.....#.##..#.")
    , (vec "#...#...###..####....##.")
    , (vec ".#.##...#.##.#.#.###...#")
    , (vec "#.###.#..####...##..#...")
    , (vec "#.###...#.##...#.######.")
    , (vec ".###.###.#######..#####.")
    , (vec "..##.#..#..#.#######.###")
    , (vec "#.#..##.########..#..##.")
    , (vec "#.#####..#.#...##..#....")
    , (vec "#....##..#.#########..##")
    , (vec "#...#.....#..##...###.##")
    , (vec "#..###....##.#...##.##.#")
    ]
)

;(find-monsters p2-sample-picture)
(def p2-sample-modified (find-monsters-and-replace p2-sample-picture))
(doseq [line p2-sample-modified] (println (apply str line)))
(let [ grouped (group-by identity (for [line p2-sample-modified, c line] c))
     , with-counts (into {}
                       (for [[k v] grouped] [k (count v)])
                   )
     ]
    (println with-counts)
)

;(println "checking 0")
;(find-monsters (rotate-tile p2-picture 0))
;(println "checking 90")
;(find-monsters (rotate-tile p2-picture 90))
;(println "checking 180")
;(find-monsters (rotate-tile p2-picture 180))
;(println "checking 270")
;(find-monsters (rotate-tile p2-picture 270))
;(println "checking h-mirrored 0")
;(find-monsters (rotate-tile (mirror-tile-horiz p2-picture) 0))    <- it's this one
;(println "checking h-mirrored 90")
;(find-monsters (rotate-tile (mirror-tile-horiz p2-picture) 90))
;(println "checking h-mirrored 180")
;(find-monsters (rotate-tile (mirror-tile-horiz p2-picture) 180))
;(println "checking h-mirrored 270")
;(find-monsters (rotate-tile (mirror-tile-horiz p2-picture) 270))


;(find-monsters (mirror-tile-horiz p2-picture))


(def p2-picture-modified (find-monsters-and-replace (mirror-tile-horiz p2-picture)))
(doseq [line p2-picture-modified] (println (apply str line)))
(let [ grouped (group-by identity (for [line p2-picture-modified, c line] c))
     , with-counts (into {}
                       (for [[k v] grouped] [k (count v)])
                   )
     ]
    (println with-counts)
)

; p2 answer = 2009
; dealing with potentially-overlapping sea monsters turned out to be a non-issue
