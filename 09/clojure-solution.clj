
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
         , stream (nthrest stream 25)
         ]
        (for [x stream]
            
        )
    )
)
