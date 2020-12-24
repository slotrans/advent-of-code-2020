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

(defn trace-directions [directions coords] ; -> coords
    (assert (= 0 (+ (coords 0) (coords 1) (coords 2))) "Invalid cube coordinates!") ; cube coords satisfy x + y + z = 0
    (let [direction (first directions)]
        (if (nil? direction)
            coords
            (let [coord-deltas (direction-deltas direction)]
                (trace-directions 
                    (rest directions)
                    (vec (map + coords coord-deltas))
                )
            )
        )
    )
)

(defn flip-tile [grid coords]
    (if (not (contains? grid coords))
        :black ; tiles not in the sparse grid are assumed to be :white
        (let [tile-state (grid coords)]
            (if (= :white tile-state)
                :black
                :white
            )
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
;(def sample-directions (directions-from-input "sample-input24"))
;
;(def sample-grid (create-sparse-grid sample-directions {}))
;(doseq [i sample-grid]
;    (println i)
;)
;(println (frequencies (vals sample-grid)))


(def input-directions (directions-from-input "input24"))

(def input-grid (create-sparse-grid input-directions {}))

(println (frequencies (vals input-grid)))
;answer=244
