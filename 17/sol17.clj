(ns net.blergh.advent2020
    (:require [clojure.string :as str]
    )
)

;; since the space is infinite, we need a sparse representation
;; use a map where the keys are [x y z] coordinate triples and the values are \. or \# (active or inactive)
;; for an initial plane at a constant z: 
;;   treat the upper-left corner is the x,y origin, 
;;   x increases as you go right, y increases as you go down
;; the sample 3x3 plane is therefore...
;; { [0 0 0] \.   ; start of first row
;; , [1 0 0] \#
;; , [2 0 0] \.
;; , [0 1 0] \.   ; start of second row
;; , [1 1 0] \.
;; , [2 1 0] \#
;; , [0 2 0] \#   ; start of third row
;; , [1 2 0] \#
;; , [2 2 0] \#
;; }

(defn space-from-input [input]
    (into
        {}
        (flatten
            (map-indexed
                (fn [y row]
                    (map-indexed
                        (fn [x character]
                            {[x y 0] character}
                        )
                        (vec row) ; convert string to vector of characters
                    )
                )
                (str/split-lines input)
            )
        )
    )
)


;; Concept taken from #11, adjusted for 3 dimensions and a sparse representation of space.
;; Cubes not present in space are presumed to be inactive, hence the default \. in each `get` call.
;;
;; I'm sure there's a more compact way of expressing this, but unrolling it like this
;; makes it easy to *see* what's happening
(defn get-adjacent-cubes-old [coords space]
    (let [[x y z] coords]
        [
            (get space [(- x 1) (- y 1) (- z 1)] \.)
            (get space [   x    (- y 1) (- z 1)] \.)
            (get space [(+ x 1) (- y 1) (- z 1)] \.)

            (get space [(- x 1)    y    (- z 1)] \.)
            (get space [   x       y    (- z 1)] \.)
            (get space [(+ x 1)    y    (- z 1)] \.)

            (get space [(- x 1) (+ y 1) (- z 1)] \.)
            (get space [   x    (+ y 1) (- z 1)] \.)
            (get space [(+ x 1) (+ y 1) (- z 1)] \.)


            (get space [(- x 1) (- y 1)    z   ] \.)
            (get space [   x    (- y 1)    z   ] \.)
            (get space [(+ x 1) (- y 1)    z   ] \.)

            (get space [(- x 1)    y       z   ] \.)
           ;(get space [   x       y       z   ] \.)
            (get space [(+ x 1)    y       z   ] \.)

            (get space [(- x 1) (+ y 1)    z   ] \.)
            (get space [   x    (+ y 1)    z   ] \.)
            (get space [(+ x 1) (+ y 1)    z   ] \.)


            (get space [(- x 1) (- y 1) (+ z 1)] \.)
            (get space [   x    (- y 1) (+ z 1)] \.)
            (get space [(+ x 1) (- y 1) (+ z 1)] \.)

            (get space [(- x 1)    y    (+ z 1)] \.)
            (get space [   x       y    (+ z 1)] \.)
            (get space [(+ x 1)    y    (+ z 1)] \.)

            (get space [(- x 1) (+ y 1) (+ z 1)] \.)
            (get space [   x    (+ y 1) (+ z 1)] \.)
            (get space [(+ x 1) (+ y 1) (+ z 1)] \.)
        ]
    )
)

(defn get-adjacent-coords [coords] ; -> vec of coords
    (let [[x y z] coords]
        [
            [(- x 1) (- y 1) (- z 1)]
            [   x    (- y 1) (- z 1)]
            [(+ x 1) (- y 1) (- z 1)]

            [(- x 1)    y    (- z 1)]
            [   x       y    (- z 1)]
            [(+ x 1)    y    (- z 1)]

            [(- x 1) (+ y 1) (- z 1)]
            [   x    (+ y 1) (- z 1)]
            [(+ x 1) (+ y 1) (- z 1)]


            [(- x 1) (- y 1)    z   ]
            [   x    (- y 1)    z   ]
            [(+ x 1) (- y 1)    z   ]

            [(- x 1)    y       z   ]
           ;[   x       y       z   ]
            [(+ x 1)    y       z   ]

            [(- x 1) (+ y 1)    z   ]
            [   x    (+ y 1)    z   ]
            [(+ x 1) (+ y 1)    z   ]


            [(- x 1) (- y 1) (+ z 1)]
            [   x    (- y 1) (+ z 1)]
            [(+ x 1) (- y 1) (+ z 1)]

            [(- x 1)    y    (+ z 1)]
            [   x       y    (+ z 1)]
            [(+ x 1)    y    (+ z 1)]

            [(- x 1) (+ y 1) (+ z 1)]
            [   x    (+ y 1) (+ z 1)]
            [(+ x 1) (+ y 1) (+ z 1)]
        ]
    )
)

(defn get-adjacent-cubes [coords space] ; -> seq of states (\# or \.)
    (map
        #(get space % \.)
        (get-adjacent-coords coords)
    )
)


;; Concept taken from #11.
;; Adjusted for 3-dimensional space and different rules.
(defn next-cube-state [coords space] ; -> state (\# or \.)
    (let [ this-cube (get space coords)
         , adjacent-cubes (get-adjacent-cubes coords space)
         , active-adjacent-cubes (count (filter #(= \# %) adjacent-cubes))
         ]
        (cond
            (and (= \# this-cube)               ; cube is active
                 (<= 2 active-adjacent-cubes 3) ; 2 or 3 active cubes adjacent
            )
            \# ; remains active
            (and (= \. this-cube)               ; cube is inactive
                 (= active-adjacent-cubes 3)    ; exactly 3 active cubes adjacent
            )
            \# ; becomes active
            :else \. ; becomes inactive
        )    
    )
)


;; See #24, which I did before this one (long story).
;; Because our space is sparse, the cellular automata rules may imply that cubes *not in our space*
;; should be activated. Therefore, before we step the simulation, we need to make sure that our space
;; includes cubes for all neighbors of active cubes.
;; Do this by conjuring a space with inactive cubes surrounding the coordinates of each active cube
;; from our real space, then merging our real space into the conjured space.
(defn summon-inactive-neighbors [coords] ; -> space
    (into
        {}
        (for [point (get-adjacent-coords coords)]
            {point \.}
        )
    )
)

(defn ensure-neighbors [space] ; -> space
    (merge
        ;; conjured space
        (into 
            {}
            (map
                #(summon-inactive-neighbors (key %))
                (filter
                    #(= \# (val %))
                    space
                )
            )
        )
        ;; known space will take precedence in the merged output
        space
    )
)


(defn step-simulation [space] ; -> space
    (let [expanded-space (ensure-neighbors space)]
        (into
            {}
            (map
                (fn [coords]
                    {coords (next-cube-state coords expanded-space)}
                )
                (keys expanded-space)
            )
        )
    )
)


(defn simulate-n-cycles [space n] ; -> space
    (nth (iterate step-simulation space) n)
)



; funky formatting here is so we can visualize the input text properly aligned
(def sample-input
(str/replace

".#.
 ..#
 ###"

" " ""
))

(def sample-space (space-from-input sample-input))
(def sample-after-6 (simulate-n-cycles sample-space 6))
(println "(sample) count of active cubes after 6 steps" (count (filter #(= \# (val %)) sample-after-6)))


(def input17 (slurp "input17.txt"))
(def input17-space (space-from-input input17))
(def input17-after-6 (simulate-n-cycles input17-space 6))
(println "(p1) count of active cubes after 6 steps" (count (filter #(= \# (val %)) input17-after-6)))
