
(ns net.blergh.advent2020
    (:require [clojure.string :as str]
    )
)


(def sample-input
"35
20
15
25
47
40
62
55
65
95
102
117
150
182
127
219
299
277
309
576")

(def sample-stream 
    (vec
        (for [line (str/split-lines sample-input)]
            (Integer/parseInt line)
        )
    )
)

(println sample-stream)

(defn is-valid? [number window]
    (some
        (fn [x] (= x number))
        (for [ x window
             , y window
             :when (not (= x y))
             ]
             (+ x y)
        )
    )
)

(println (is-valid? 40 [35 20 15 25 47]))

(println (is-valid? 62 [20 15 25 47 40]))

(println (is-valid? 127 [95 102 117 150 182]))


(defn find-first-invalid [stream]
    (let [ preamble (take 25 stream)
         , rest-of-stream (nthrest stream 25)
         , number-to-test (first rest-of-stream)
         ]
        ;(println (str "testing " number-to-test))
        (if (not (is-valid? number-to-test preamble))
            number-to-test
            (find-first-invalid (rest stream))
        )
    )
)

(def input09 (slurp "input09"))

(def input-stream 
    (for [i (str/split-lines input09)]
        (Integer/parseInt i)
    )
)

(def part1-answer (find-first-invalid input-stream))
(println (str "(p1 answer) first invalid number in input stream: " part1-answer))
;answer=258585477
