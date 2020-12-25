;The tiles are all white on one side and black on the other. 
;They start with the white side facing up.
;
;...a list of the tiles that need to be flipped over (your puzzle input). 
;Each line in the list identifies a single tile that needs to be flipped by giving a series of steps 
;starting from a reference tile in the very center of the room. 
;(Every line starts from the same reference tile.)
;
;Because the tiles are hexagonal, every tile has six neighbors: 
;east, southeast, southwest, west, northwest, and northeast. 
;These directions are given in your list, respectively, as e, se, sw, w, nw, and ne. 
;A tile is identified by a series of these directions with no delimiters; 
;for example, "esenee" identifies the tile you land on if you start at the reference tile and then 
;move one tile east, one tile southeast, one tile northeast, and one tile east.
;
;Each time a tile is identified, it flips from white to black or from black to white. 
;Tiles might be flipped more than once. For example, a line like "esew" flips a tile immediately 
;adjacent to the reference tile, and a line like nwwswee flips the reference tile itself.

(ns net.blergh.advent2020
    (:require [clojure.string :as str])
)


; in (x, y, z) cube coordinates... (https://www.redblobgames.com/grids/hexagons/)
(def direction-deltas
    { :ne [+1  0 -1]
    ,  :e [+1 -1  0]
    , :se [ 0 -1 +1]
    , :sw [-1  0 +1]
    ,  :w [-1 +1  0]
    , :nw [ 0 +1 -1]
    }
)

(defn add-coords [a b]
    (vec (map + a b))
)

(defn trace-directions [directions coords] ; -> coords
    (assert (= 0 (+ (coords 0) (coords 1) (coords 2))) "Invalid cube coordinates!") ; cube coords satisfy x + y + z = 0
    (let [direction (first directions)]
        (if (nil? direction)
            coords
            (let [coord-deltas (direction-deltas direction)]
                (trace-directions 
                    (rest directions)
                    (add-coords coords coord-deltas)
                )
            )
        )
    )
)

(defn flip-tile [grid coords]
    (let [tile-state (get grid coords :white)]
        (if (= :white tile-state)
            :black
            :white
        )
    )
)

(defn create-sparse-grid [directions-list grid] ; -> grid (a map of coords -> :white/:black)
    (let [directions (first directions-list)]
        (if (nil? directions)
            grid
            (let [tile-coords (trace-directions directions [0 0 0])]
                (create-sparse-grid 
                    (rest directions-list) 
                    (assoc grid tile-coords (flip-tile grid tile-coords))
                )
            )
        )
    )
)

(defn directions-from-input [filename]
    (for [line (str/split-lines (slurp filename))]
        (map keyword (re-seq #"e|w|se|sw|ne|nw" line))
    )
)

; run the sample
(def sample-directions (directions-from-input "sample-input24"))

(def sample-grid (create-sparse-grid sample-directions {}))
;(doseq [i sample-grid]
;    (println i)
;)
;(println (frequencies (vals sample-grid)))


(def input-directions (directions-from-input "input24"))

(def input-grid (create-sparse-grid input-directions {}))

(println "part1 answer")
(println (frequencies (vals input-grid)))
;answer=244


;part 2
(defn count-adjacent-black-tiles [grid coords]
    (count 
        (filter 
            #(= :black %)
            [ (get grid (add-coords coords (direction-deltas :ne)) :white)
            , (get grid (add-coords coords (direction-deltas  :e)) :white)
            , (get grid (add-coords coords (direction-deltas :se)) :white)
            , (get grid (add-coords coords (direction-deltas :sw)) :white)
            , (get grid (add-coords coords (direction-deltas  :w)) :white)
            , (get grid (add-coords coords (direction-deltas :nw)) :white)
            ]
        )
    )
)

; the grid we get from following the directions is sparse, which creates a problem
; for applying cellular automata rules to each tile
; since the rules are based on "# of black neighbors" it should be sufficient to
; fill in neighboring tiles for every black tile in the grid
;
; do this by conjuring white tiles for every neighbor, then merging the actual grid into that
(defn ensure-neighbors [grid] ; -> grid
    (merge
        (into
            {}
            (for [[coords color] (seq grid)]
                (let [ neighbor-coords [ (add-coords coords (direction-deltas :ne))
                                       , (add-coords coords (direction-deltas  :e))
                                       , (add-coords coords (direction-deltas :se))
                                       , (add-coords coords (direction-deltas :sw))
                                       , (add-coords coords (direction-deltas  :w))
                                       , (add-coords coords (direction-deltas :nw))
                                       ]
                     ]
                    (into
                        {}
                        (for [neighbor neighbor-coords]
                            [neighbor :white]
                        )
                    )
                )
            )
        )
        grid
    )
)

;(println "sample grid, sparse")
;(doseq [i sample-grid]
;    (println i)
;)
;(println (frequencies (vals sample-grid)))
;(println "sample grid, less sparse")
;(def less-sparse-sample-grid (ensure-neighbors sample-grid))
;(doseq [i less-sparse-sample-grid]
;    (println i)
;)
;(println (frequencies (vals less-sparse-sample-grid)))


;adapted from #11
(defn next-tile-state [grid coords]
    (let [ this-tile-color (get grid coords)
         , num-adjacent-black-tiles (count-adjacent-black-tiles grid coords)
         ]
        (cond 
            (and (= :black this-tile-color)
                 (or (= num-adjacent-black-tiles 0)
                     (> num-adjacent-black-tiles 2)
                 ) ; 0 or >2 black neighbors -> white
            )
            :white
            (and (= :white this-tile-color)
                 (= num-adjacent-black-tiles 2) ; exactly 2 black neighbors -> black
            )
            :black
            :else this-tile-color
        )    
    )
)

;adapted from #11
(defn step-simulation [grid]
    (let [less-sparse-grid (ensure-neighbors grid)]
        (into
            {}
            (for [coords (keys less-sparse-grid)]
                [coords (next-tile-state less-sparse-grid coords)]
            )    
        )
    )
)

(defn simulate-n-times [grid n]
    (println (str n " steps remaining"))
    (if (= n 0)
        grid
        (simulate-n-times (step-simulation grid) (- n 1))
    )
)

;run the sample
;(def sample-grid-after-100 (simulate-n-times sample-grid 100))
;(println (frequencies (vals sample-grid-after-100)))

(def input-grid-after-100 (simulate-n-times input-grid 100))
(println (frequencies (vals input-grid-after-100)))
;answer=3665
