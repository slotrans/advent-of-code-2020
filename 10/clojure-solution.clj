;Each of your joltage adapters is rated for a specific output joltage (your puzzle input). 
;Any given adapter can take an input 1, 2, or 3 jolts lower than its rating and still produce 
;its rated output joltage.

;In addition, your device has a built-in joltage adapter rated for 3 jolts higher than the 
;highest-rated adapter in your bag. (If your adapter list were 3, 9, and 6, your device's built-in 
;adapter would be rated for 12 jolts.)

;Treat the charging outlet near your seat as having an effective joltage rating of 0.

(ns net.blergh.advent2020
    (:require [clojure.string :as str])
)


(def sample-input1 [1 4 5 6 7 10 11 12 15 16 19])
(def sample-device-rating1
    (+ 3 (apply max sample-input1))
)
(def sample-adapter-chain1 (conj sample-input1 sample-device-rating1))

(def sample-input2 (vec (sort [28 33 18 42 31 14 46 20 48 47 24 23 49 45 19 38 39 11 1 32 25 35 8 17 7 9 4 2 34 10 3])))
(def sample-device-rating2
    (+ 3 (apply max sample-input2))
)
(def sample-adapter-chain2 (conj sample-input2 sample-device-rating2))

(defn measure-differences [adapter-chain]
    (frequencies
        (loop [ output 0
              , chain adapter-chain
              , differences []
              ]
            (if (empty? chain)
                differences
                (let [ next-adapter (first chain)
                     , diff (- next-adapter output)
                     ]
                    (recur
                        next-adapter
                        (rest chain)
                        (conj differences diff)
                    )
                )
            )
        )
    )
)

(println (str "sample chain 1: " sample-adapter-chain1))
(println (str "joltage differences in sample chain 1 ({jolt->cnt}): " (measure-differences sample-adapter-chain1)))

(println (str "sample chain 2: " sample-adapter-chain2))
(println (str "joltage differences in sample chain 2 ({jolt->cnt}): " (measure-differences sample-adapter-chain2)))


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
(def input-adapter-chain (conj input-joltages input-device-rating))

(def input-differences (measure-differences input-adapter-chain))
(println (str "joltage differences in input ({jolt->cnt}): " input-differences))
(def part1-answer
    (*
        (get input-differences 1)
        (get input-differences 3)
    )
)
(println (str "(p1 answer) 1j differences * 3j differences = " part1-answer))
;answer=1914
