(ns net.blergh.advent2020
)

; `seed` is the initial recency map.
; Keys are spoken numbers.
; Vals are a vector of turns on which each number has been spoken.
;
; [0 3 6]
; ->
; { 0 [1]
;   3 [2]
;   6 [3]
; }
(defn get-seed [starting-numbers]
    (into
        {}
        (map-indexed
            (fn [idx item] {item [(+ idx 1)]})
            starting-numbers
        )
    )
)


(defn play-to-turn [starting-numbers target]
    (loop [ recency-map (get-seed starting-numbers)
          , turn (+ 1 (count starting-numbers))
          , last-number (last starting-numbers)
          ]
        ;(println recency-map)
        (if (> turn target)
            last-number
            (let [ spoken-on-turn-list (get recency-map last-number)
                 , is-first-time-spoken? (= 1 (count spoken-on-turn-list))
                 , last-two-turns-spoken (take-last 2 spoken-on-turn-list)
                 , number-to-speak (if is-first-time-spoken?
                                       0
                                       (- (second last-two-turns-spoken) (first last-two-turns-spoken))
                                   )
                 ]
                ;(println (str "turn " turn 
                ;              ": last-number=" last-number 
                ;              ", last-two-turns-spoken=" last-two-turns-spoken
                ;              ", first?=" is-first-time-spoken?
                ;              ", therefore speak " number-to-speak))
                (recur (assoc recency-map number-to-speak (conj (get recency-map number-to-speak []) turn))
                       (+ 1 turn)
                       number-to-speak
                )
            )
        )
    )
)

(def explanation-sample [0 3 6])
(println (str "explanation sample " explanation-sample " last of 10 turns: " (play-to-turn explanation-sample 10)))
(println (str "explanation sample " explanation-sample " last of 2020 turns: " (play-to-turn explanation-sample 2020)))



; Given the starting numbers 1,3,2, the 2020th number spoken is 1.
; Given the starting numbers 2,1,3, the 2020th number spoken is 10.
; Given the starting numbers 1,2,3, the 2020th number spoken is 27.
; Given the starting numbers 2,3,1, the 2020th number spoken is 78.
; Given the starting numbers 3,2,1, the 2020th number spoken is 438.
; Given the starting numbers 3,1,2, the 2020th number spoken is 1836.

(def sample01 [1 3 2])
(def sample02 [2 1 3])
(def sample03 [1 2 3])
(def sample04 [2 3 1])
(def sample05 [3 2 1])
(def sample06 [3 1 2])

(println "sample01:" sample01 (play-to-turn sample01 2020))
(println "sample02:" sample02 (play-to-turn sample02 2020))
(println "sample03:" sample03 (play-to-turn sample03 2020))
(println "sample04:" sample04 (play-to-turn sample04 2020))
(println "sample05:" sample05 (play-to-turn sample05 2020))
(println "sample06:" sample06 (play-to-turn sample06 2020))


(def input15 [15 5 1 4 7 0])
(println "input15:" input15 (play-to-turn input15 2020))
