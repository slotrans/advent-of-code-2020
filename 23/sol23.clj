(ns net.blergh.advent2020
    (:require [clojure.string :as str]
    )
)


(defn get-destination [curcup others] ; -> label of "destination cup"
    (loop [num-to-try (dec curcup)]
        (if (contains? (set others) num-to-try)
            num-to-try
            (if (every? #(< num-to-try %) others)
                (apply max others)
                (recur (dec num-to-try))
            )
        )
    )
)


(defn get-destination2 [curcup picked-up others]
    (loop [num-to-try (dec curcup)]
        (cond
            (< num-to-try 1)  
                (apply max others)
            
            (.contains picked-up num-to-try)
                (recur (dec num-to-try))

            :else
                num-to-try
        )
    )
)


(defn step-game [state] ; -> new state
    (let [ current-cup (first state)
         , picked-up-cups (take 3 (rest state))
         , remaining-cups (drop 3 (rest state))
         ;, destination-cup (get-destination current-cup remaining-cups)
         , destination-cup (get-destination2 current-cup picked-up-cups remaining-cups)
         , destination-index (.indexOf remaining-cups destination-cup)
         , [left-part right-part] (split-at (inc destination-index) remaining-cups) ; split-at takes a count not an index, so it needs to be +1
         ]
        (concat left-part picked-up-cups right-part [current-cup])
    )
)


(defn clockwise-from-one [cups]
    (let [ index-of-one (.indexOf cups 1)
         , [left-part right-part] (split-at (inc index-of-one) cups)
         ]
        (apply str (concat right-part (butlast left-part)))
    )
)


(def sample-input [3 8 9 1 2 5 4 6 7])
(def sample-after-10 (vec (nth (iterate step-game sample-input) 10)))
(println (str "sample after 10 turns: " sample-after-10))
(def sample-after-100 (vec (nth (iterate step-game sample-input) 100)))
(println (str "sample after 100 turns: " sample-after-100))
(println (str "sample answer: " (clockwise-from-one sample-after-100)))


(def input23 [8 7 2 4 9 5 1 3 6])
(def input23-after-100 (vec (nth (iterate step-game input23) 100)))
(println (str "p1 input after 100 turns: " input23-after-100))
(println (str "p1 answer: " (clockwise-from-one input23-after-100))) ; 27865934


;; PART 2 STUFF


(defn two-cups-following-one [cups]
    (let [ index-of-one (.indexOf cups 1)
         , [left-part right-part] (split-at (inc index-of-one) cups)
         , rearranged-cups (concat right-part left-part)
         ]
        (take 2 rearranged-cups)
    )  
)


(def p2-sample-input (vec (concat sample-input (range 10 1000001))))
;(def p2-sample-after-10M (vec (nth (iterate step-game p2-sample-input) 10000000)))
;(println (str "p2 sample after 10,000,000 turns: " p2-sample-after-10M))
;(println (str "p2 sample two cups: " (two-cups-following-one p2-sample-after-10M)))

;(def n-turns 100)
;(def p2-sample-after-X (vec (nth (iterate step-game p2-sample-input) n-turns)))
;(println (str "p2 sample after " n-turns " turns: " (vec (take 20 p2-sample-after-X)) "..."))


