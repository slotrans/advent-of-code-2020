(ns net.blergh.advent2020
)


(defn to-ringlist [v]
    (into
        {}
        (for [i (range 0 (count v))]
            {
                (get v i)                 ; key is the cup# at this position
                (get v (inc i) (get v 0)) ; val is the cup# at the next position, and loop back to the beginning
            }
        )
    )
)
(comment 
    [3 8 9 1 2 5 4 6 7]
    "becomes..."
    { 3 8
    , 8 9
    , 9 1
    , 1 2
    , 2 5
    , 5 4
    , 4 6
    , 6 7
    , 7 3 ; observe that it loops back on itself
    }
    "...though the ordering is lost"
)


(defn pick-up-cups [ringlist current] ; -> {:picked-up-cups [0 1 2] :new-ringlist modified-ringlist}
    (let [ current-node (find ringlist current)
         , picked-up-0 (val current-node)
         , picked-up-1 (get ringlist picked-up-0)
         , picked-up-2 (get ringlist picked-up-1)
         , new-val-for-current-node (get ringlist picked-up-2)
         ]
        { :picked-up-cups [picked-up-0 picked-up-1 picked-up-2]
        , :new-ringlist 
            (-> ringlist
                (dissoc ,,, picked-up-0 picked-up-1 picked-up-2)
                (assoc ,,, current new-val-for-current-node)
            )
        }
    )
)
(comment "an illustration of picking up 3 cups following cup #3"
{ 3 8  ;  3 2 <- modified  
, 8 9  ;      <- removed
, 9 1  ;      <- removed
, 1 2  ;      <- removed
, 2 5  ;  2 5
, 5 4  ;  5 4
, 4 6  ;  4 6
, 6 7  ;  6 7
, 7 3  ;  7 3
}
)


(defn insert-picked-up-cups [ringlist picked-up-cups afterval] ; -> modified ringlist
    (let [current-node (find ringlist afterval)]
        (assoc
            ringlist
            (key current-node)     (get picked-up-cups 0)
            (get picked-up-cups 0) (get picked-up-cups 1)
            (get picked-up-cups 1) (get picked-up-cups 2)
            (get picked-up-cups 2) (val current-node)
        )
    )
)
(comment "an illustration of inserting a set of 3 cups after cup #2"
{ 3 2  ;  3 2
, 2 5  ;  2 8 <- modified
       ;  8 9 <- added
       ;  9 1 <- added
       ;  1 5 <- added
, 5 4  ;  5 4
, 4 6  ;  4 6
, 6 7  ;  6 7
, 7 3  ;  7 3
}
)




(comment "the slow version from p1"
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
)

(def min-num 1)
(def max-num 9)
(defn get-destination [current-cup picked-up-cups]
    (loop [num-to-try (dec current-cup)]
        (cond
            (< num-to-try min-num)  
                (recur max-num)
            
            (.contains picked-up-cups num-to-try)
                (recur (dec num-to-try))

            :else
                num-to-try
        )
    )
)


;; `current` is the value (not position) of the "current" cup
(defn step-game [[ringlist current]] ; -> [new ringlist, new current]
    ;(println (str "current=" current ", ringlist=" ringlist))
    ;(println (str "current=" current))
    (let [ {:keys [picked-up-cups new-ringlist]} (pick-up-cups ringlist current)
         , destination-cup (get-destination current picked-up-cups)
         , ringlist-after-reinsertion (insert-picked-up-cups new-ringlist picked-up-cups destination-cup)
         , next-current (get ringlist-after-reinsertion current)
         ]
        [ringlist-after-reinsertion next-current]
    )
)


(defn part-1-answer [ringlist]
    (loop [ node (find ringlist 1)
          , remaining ringlist
          , out-vec []
          ]
        ;(println (str "node=" node ", remaining=" remaining ", out-vec=" out-vec))
        (if (nil? node)
            (apply str out-vec)
            (recur
                (find remaining (val node))
                (dissoc remaining (key node))
                (if (= 1 (val node)) ; when we loop back around to 1, don't add it to the output
                    out-vec
                    (conj out-vec (val node))
                )
            )
        )
    )
)


(def sample-input [3 8 9 1 2 5 4 6 7])
(def sample-ringlist (to-ringlist sample-input))

;(def sample-after-10 (nth (iterate step-game [sample-ringlist (first sample-input)]) 10))
;(println (str "sample after 10: " sample-after-10))
;(def sample-after-100 (nth (iterate step-game [sample-ringlist (first sample-input)]) 100))
;(println (str "sample after 100: " sample-after-100))
;(println (str "sample answer: ") (part-1-answer (first sample-after-100))) ; 67384529


(def p1-input [8 7 2 4 9 5 1 3 6])
;(def p1-ringlist (to-ringlist p1-input))
;(def p1-after-100 (nth (iterate step-game [p1-ringlist (first p1-input)]) 100))
;(println (str "p1 after 100: " p1-after-100))
;(println (str "p1 answer: ") (part-1-answer (first p1-after-100))) ; 27865934


;; PART 2

(defn two-cups-following-one [ringlist]
    (let [ node-1 (find ringlist 1)
         , next-node (find ringlist (val node-1))
         ]
        next-node
    )
)


(defn run-n-times [ringlist current n]
    (loop [ [ringlist-arg current-arg] [ringlist current]
          , i 1
          ]
        (if (= 0 (mod i 10000)) (println i))
        (if (> i n)
            [ringlist-arg current-arg]
            (recur
                (step-game [ringlist-arg current-arg])
                (inc i)
            )
        )
    )   
)


(alter-var-root (var max-num) (constantly (* 1000 1000)))
(println "assembling input...")
(def p2-input (vec (concat p1-input (range 10 (inc max-num)))))
(def p2-ringlist (to-ringlist p2-input))
(println "stepping...")
;(def p2-after-10M (nth (iterate step-game [p2-ringlist (first p2-input)]) (* 10 1000 1000)))
(def p2-after-running (run-n-times p2-ringlist (first p2-input) (* 10 1000 1000)))
(def p2-answer-inputs (two-cups-following-one (first p2-after-running)))
(println (str "two cups after #1: " p2-answer-inputs)) ; [267349 639000]
(println (str "p2 answer: " (* (first p2-answer-inputs) (second p2-answer-inputs)))) ; 170836011000
