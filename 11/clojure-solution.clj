(ns net.blergh.advent2020
    (:require [clojure.string :as str])
)

; All decisions are based on the number of occupied seats adjacent to a given seat 
; (one of the eight positions immediately up, down, left, right, or diagonal from the seat).
; The following rules are applied to every seat simultaneously:
; 
; - If a seat is empty (L) and there are no occupied seats adjacent to it, the seat becomes occupied.
; - If a seat is occupied (#) and four or more seats adjacent to it are also occupied, the seat becomes empty.
; - Otherwise, the seat's state does not change.
; 
; Floor (.) never changes; seats don't move, and nobody sits on the floor.

;L.LL.LL.LL
;LLLLLLL.LL
;L.L.L..L..
;LLLL.LL.LL
;L.LL.LL.LL
;L.LLLLL.LL
;..L.L.....
;LLLLLLLLLL
;L.LLLLLL.L
;L.LLLLL.LL
(def sample-input11 (slurp "sample-input11"))

(def sample-grid
    (into
        []
        (for [line (str/split-lines sample-input11)]
            (vec line)
        )
    )
)

;(doseq [row sample-grid]
;    (println row)
;)

; from puzzle #3, adjusted to zero-based indexing, and to return \. for a non-existent location
(defn index-by-coords [grid x y]
    (or
        (let [row (get grid y)]
            (get row x)
        )
        \.
    )
)

(defn get-adjacent-seats [grid x y]
    [
        (index-by-coords grid (- x 1) (- y 1))
        (index-by-coords grid    x    (- y 1))
        (index-by-coords grid (+ x 1) (- y 1))

        (index-by-coords grid (- x 1)    y   )
       ;(index-by-coords grid    x       y   )
        (index-by-coords grid (+ x 1)    y   )

        (index-by-coords grid (- x 1) (+ y 1))
        (index-by-coords grid    x    (+ y 1))
        (index-by-coords grid (+ x 1) (+ y 1))
    ]
)

(defn next-seat-state [grid x y]
    (let [ this-seat (index-by-coords grid x y)
         , adjacent-seats (get-adjacent-seats grid x y)
         ]
        (cond 
            (and (= \L this-seat)                                ; seat is empty
                 (= (count (filter #(= \# %) adjacent-seats)) 0) ; no occupied seats adjacent (could also use not-any?)
            )
            \# ; becomes occupied
            (and (= \# this-seat)                                 ; seat is occupied
                 (>= (count (filter #(= \# %) adjacent-seats)) 4) ; >= 4 occupied seats adjacent
            )
            \L ; becomes empty
            :else this-seat ; unchanged
        )    
    )
)

(defn step-simulation [grid]
    (let [ ysize (count grid)
         , xsize (count (get grid 0))
         ]
        (into
            []
            (for [y (range 0 ysize)]
                (into
                    []
                    (for [x (range 0 xsize)]
                        (next-seat-state grid x y)
                    )
                )
            )    
        )
    )
)

(defn simulate-until-stable [grid]
    (let [next-grid (step-simulation grid)]
        (if (= grid next-grid)
            grid
            (simulate-until-stable next-grid)
        )
    )
)


;(def stable-sample-grid (simulate-until-stable sample-grid))
;(doseq [row stable-sample-grid]
;    (println row)
;)
;
;(def stable-sample-occupied-seats
;    (count (filter #(= \# %) (flatten stable-sample-grid)))
;)
;(println stable-sample-occupied-seats)

(def input11 (slurp "input11"))

(def input-grid
    (into
        []
        (for [line (str/split-lines input11)]
            (vec line)
        )
    )
)

(def stable-input-grid (simulate-until-stable input-grid))
(doseq [row stable-input-grid]
    (println row)
)

(def stable-input-occupied-seats
    (count (filter #(= \# %) (flatten stable-input-grid)))
)
(println (str "part 1 answer (occupied seats): " stable-input-occupied-seats))
;answer=2470

