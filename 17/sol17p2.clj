(ns net.blergh.advent2020
    (:require [clojure.string :as str]
    )
)

;; NOTES ON PART 2
;;
;; It's pleasing how much of the code from part 1 is reusable, by virtue of
;; not depending on the details (i.e. number of dimensions) of coordinates.
;; But, in the places where code is specific to coordinates, making that code
;; general across any number of dimensions would be ugly.
;;
;; The "right answer" is probably to put all the generic functions into their
;; own namespace, and then have namespaces for the 3- and 4-dimensional specific
;; parts. I judge that to be excessive for purposes of this exercise, so I'm solving
;; part 2 in a separate file copy-and-pasted from part 1.



;; since the space is infinite, we need a sparse representation
;; use a map where the keys are [x y z w] coordinate 4-tuples and the values are \. or \# (active or inactive)
;; for an initial plane at a constant z and constant w: 
;;   treat the upper-left corner is the x,y origin, 
;;   x increases as you go right, y increases as you go down
;; the sample 3x3 plane is therefore...
;; { [0 0 0 0] \.   ; start of first row
;; , [1 0 0 0] \#
;; , [2 0 0 0] \.
;; , [0 1 0 0] \.   ; start of second row
;; , [1 1 0 0] \.
;; , [2 1 0 0] \#
;; , [0 2 0 0] \#   ; start of third row
;; , [1 2 0 0] \#
;; , [2 2 0 0] \#
;; }

(defn space-from-input [input]
    (into
        {}
        (flatten
            (map-indexed
                (fn [y row]
                    (map-indexed
                        (fn [x character]
                            {[x y 0 0] character}
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
;; OK, we found the limit. Unrolling this into 4 dimensions is too much, so here's a nested loop version.
(defn get-adjacent-coords [coords] ; -> vec of coords
    (let [ [x y z w] coords
         , deltas [-1 0 1]
         ]
        (filter
            #(not= % coords) ; the input point pops out of the loop but is not adjacent to itself, discard it
            (for [ dx deltas
                 , dy deltas
                 , dz deltas
                 , dw deltas
                 ]
                [(+ x dx) (+ y dy) (+ z dz) (+ w dw)]
            )
        )
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
(println "(p2) count of active cubes after 6 steps" (count (filter #(= \# (val %)) input17-after-6)))
