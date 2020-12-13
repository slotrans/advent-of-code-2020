
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

;(println sample-stream)

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

;(println (is-valid? 40 [35 20 15 25 47]))

;(println (is-valid? 62 [20 15 25 47 40]))

;(println (is-valid? 127 [95 102 117 150 182]))


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


(def target-number 258585477)
(def stream-up-to-number 
    (take-while 
        #(not (= target-number %))
        input-stream
    )
)

(defn find-range-that-sums [target stream]
    (let [ init-slice (take 2 stream)
         , init-stream (nthrest stream 2)
         ]
        (loop [ slice init-slice
              , rem-stream init-stream
              ]
            ;(println (str "testing range: " (vec slice) " / " (vec rem-stream)))
            (if (< (count slice) 2)
                nil
                (let [slice-total (apply + slice)]
                    ;(println (str "slice-total: " slice-total))
                    (if (= slice-total target)
                        slice
                        (if (> slice-total target) ; slice-total is monotonically-increasing so slice-total > target means there's no point continuing to test this stream
                            nil
                            (recur
                                (conj slice (first rem-stream))
                                (rest rem-stream)
                            )
                        )
                    )
                )
            )
        )
    )
)

(def p2-range-answer
    (loop [stream input-stream]
        (if (empty? stream)
            nil
            (do
                ;(println (str "starting search at " (first stream)))
                (if-let [answer (find-range-that-sums target-number stream)]
                    answer
                    (recur
                        (rest stream)
                    )
                )
            )
        )
    )
)
(println (str "range that sums to " target-number ": " p2-range-answer))
(def smallest (apply min p2-range-answer))
(def largest (apply max p2-range-answer))
(def part2-answer (+ smallest largest))
(println (str "smallest " smallest " + largest " largest " = " part2-answer " (p2 answer)"))
;range (27525818 16578014 22680253 25865809 14748447 14304519 14302571 12220424 11774548 11137204 12646458 11563238 13221299 16794010 9908827 13858643 9455395)
;smallest 9455395 + largest 27525818
;answer=36981213
