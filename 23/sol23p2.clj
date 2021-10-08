(ns net.blergh.advent2020
    (:import (java.util LinkedList)
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

;; this is the pure clojure version
(defn step-game-too-slow [state] ; -> new state
    (let [ current-cup (first state)
         , picked-up-cups (take 3 (rest state))
         , remaining-cups (drop 3 (rest state))
         , destination-cup (get-destination2 current-cup picked-up-cups remaining-cups)
         , destination-index (.indexOf remaining-cups destination-cup)
         , [left-part right-part] (split-at (inc destination-index) remaining-cups) ; split-at takes a count not an index, so it needs to be +1
         ]
        (concat left-part picked-up-cups right-part [current-cup])
    )
)


;; java!
(defn step-game [^LinkedList state]
    (let [ current-cup (.poll state) ; destructive!
         , picked-up-1 (.poll state)
         , picked-up-2 (.poll state)
         , picked-up-3 (.poll state)
         , picked-up-cups [picked-up-1 picked-up-2 picked-up-3]
         , destination-cup (get-destination2 current-cup picked-up-cups state)
         , destination-index (.indexOf state destination-cup)
         ]
        (doto state
            (.addAll (inc destination-index) picked-up-cups)
            (.add current-cup)
        )
    )
)


(defn two-cups-following-one [cups]
    (let [ index-of-one (.indexOf cups 1)
         , [left-part right-part] (split-at (inc index-of-one) cups)
         , rearranged-cups (concat right-part left-part)
         ]
        (vec (take 2 rearranged-cups))
    )  
)


(def sample-input [3 8 9 1 2 5 4 6 7])


(def sample-after-10 (nth (iterate step-game (LinkedList. sample-input)) 10))
(println (str "sample after 10 turns: " sample-after-10))
(def sample-after-100 (nth (iterate step-game (LinkedList. sample-input)) 100))
(println (str "sample after 100 turns: " sample-after-100))


(println "Part 2:")
(def p2-sample-input (vec (concat sample-input (range 10 1000001))))
(def p2-sample-input-mut (LinkedList. p2-sample-input))
(def n-turns 100000)
;(println (str "iterating for " n-turns " turns..."))
;(nth (iterate step-game p2-sample-input-mut) n-turns)
;(println (str "p2 sample after " n-turns " turns: " (vec (take 20 p2-sample-input-mut))))

(def input23 [8 7 2 4 9 5 1 3 6])
(def p2-input23 (concat input23 (range 10 1000001)))
(def p2-input23-mut (LinkedList. p2-input23))

(nth (iterate step-game p2-input23-mut) 10000000)
(println (str "the two cups after #1: " (two-cups-following-one p2-input23-mut)))
