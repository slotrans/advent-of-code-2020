; After finishing my solution independently, it seemed like there had to be a better way, and I took a look
; at what some others had done. There are much simpler solutions! Like, drastically simpler.
; I had little hope of building a SQL version of my initial Clojure solution, so I wanted to write up
; one of the alternate approaches which _does_ seem possible.

; Apparently this puzzle is based on the "climbing stairs problem" https://leetcode.com/problems/climbing-stairs/description/
; though the correspondence is not obvious to me. I see the spiritual similarity, but the climbing stairs problem presents
; a uniform staircase, whereas the adapter chain has a variable structure. I saw one or two solutions that exploited the
; fact that there are only ever 1-jolt or 3-jolt gaps in the input data, which makes them non-general and uninteresting.

; In general the efficient solutions all seem to directly address the "how many ways?" question rather than actually dealing
; with the structure of the adapter chain, which is what I did.

; Here is a compact python solution using the "dynamic programming" method as described in the Leetcode solutions...
;
; adapters = [0] + [1, 4, 5, 6, 7, 10, 11, 12, 15, 16, 19] + [22] # input-reading omitted for brevity
;
; ways = {0: 1}
; for adapter in adapters[1:]:
;     ways[adapter] = ways.get(adapter-1, 0) + ways.get(adapter-2, 0) + ways.get(adapter-3, 0)
; answer = ways[adapters[-1]]
;
; The key is get(..., 0) which lets us skip the stairs which don't exist, and reaching back 1, 2, and 3 units which 
; bridges us back to the largest-allowed joltage gap. In the case of a 3-jolt gap we simply carry forward the last
; ways-count without adding to it.


(ns net.blergh.advent2020
    (:require [clojure.string :as str])
)

(def input10 (slurp "input10"))
(def input-joltages 
    (vec
        (sort
            (for [line (str/split-lines input10)]
                (Integer/parseInt line)
            )
        )
    )
)
(def input-device-rating
    (+ 3 (apply max input-joltages))
)
;(def input-adapter-chain (vec (conj (concat [0] input-joltages) input-device-rating)))
(def input-adapter-chain (into [] (concat [0] input-joltages [input-device-rating])))



(defn compute-ways-map [adapters ways]
    (let [cur (first adapters)]
        (if (nil? cur)
            ways
            (let [ ways-to-this (+ (get ways (- cur 1) 0) 
                                   (get ways (- cur 2) 0)
                                   (get ways (- cur 3) 0)
                                )
                 ]
                (compute-ways-map
                    (rest adapters)
                    (assoc ways cur ways-to-this)
                )
            )
        )
    )
)

(def ways-map-for-input
    (compute-ways-map 
        (rest input-adapter-chain) ; skip the virtual "0" adapter
        {0 1} ; seed with the 1 way to reach the virtual "0" adapter
    )
)

(def part2-answer (get ways-map-for-input input-device-rating))
(println (str "ways to connect adapters: " part2-answer))
;answer=9256148959232
