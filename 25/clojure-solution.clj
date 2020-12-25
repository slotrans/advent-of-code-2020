;The handshake used by the card and the door involves an operation that transforms a subject number. 
;To transform a subject number, start with the value 1. 
;Then, a number of times called the loop size, perform the following steps:
;
;    Set the value to itself multiplied by the subject number.
;    Set the value to the remainder after dividing the value by 20201227.

(ns net.blergh.advent2020
)

(defn transform-subject-number [subject-number loop-size the-value]
    (if (= loop-size 0)
        the-value
        (let [ step1 (* the-value subject-number)
             , step2 (mod step1 20201227)
             ]
            (if (= 0 (mod loop-size 1000))
                (println loop-size)
            )
            (if (contains? #{4126658 10604480} step2)
                (println "  found " step2 " at loop size " loop-size)
            )
            (transform-subject-number subject-number (- loop-size 1) step2)
        )
    )
)

(defn transform-subject-number2 [subject-number loop-size]
    (loop [ the-value 1
          , iter 0
          ]
        ;(if (= 0 (mod iter 10000))
        ;    (println iter)
        ;)
        (if (contains? #{4126658 10604480} the-value)
            (println "  found " the-value " at loop size " iter)
        )
        (if (>= iter loop-size)
            the-value
            (let [ step1 (* the-value subject-number)
                 , step2 (mod step1 20201227)
                 ]
                (recur
                    step2
                    (+ iter 1)
                )
            )
        )
    )
)

;(println "sample")
;(doseq [i (range 1 20)]
;    (println (str i ": " (transform-subject-number 7 i 1)))
;)
;
;(println (str "door's key / card's loop size: " (transform-subject-number 17807724 8 1)))
;(println (str "card's key / door's loop size: " (transform-subject-number 5764801 11 1)))


(println "part1")
(transform-subject-number2 7 10000000)
;  found  10604480  at loop size  1568743
;  found  4126658  at loop size  9709101

(println (transform-subject-number2 10604480 9709101))
(println (transform-subject-number2 4126658 1568743))
;answer=4968512
