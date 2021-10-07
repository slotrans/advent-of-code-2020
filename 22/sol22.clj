(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
    )
)


(defn parse-input [s] ; -> map of player key to a vector of cards, with the "top" card in `first` position
    (let [ raw-parts (str/split s #"\n\n")
         , p1-raw-part (first raw-parts)
         , p2-raw-part (second raw-parts)
         , p1-raw-deck (second (str/split p1-raw-part #":\n"))
         , p2-raw-deck (second (str/split p2-raw-part #":\n"))
         ]
        { :p1 (mapv edn/read-string (str/split p1-raw-deck #"\n"))
        , :p2 (mapv edn/read-string (str/split p2-raw-deck #"\n"))
        }
    )
)


(defn game-over? [decks]
    (or (nil? (first (:p1 decks)))
        (nil? (first (:p2 decks)))
    )
)


(defn play [decks turn] ; -> final state of decks after game completion
    (println (str turn ": " decks))
    (if (game-over? decks)
        decks
        (let [ p1-card (first (:p1 decks))
             , p2-card (first (:p2 decks))
             , p1-rem-deck (vec (rest (:p1 decks)))
             , p2-rem-deck (vec (rest (:p2 decks)))
             ]
            (if (> p1-card p2-card)
                ;; p1 wins turn
                (recur { :p1 (conj p1-rem-deck p1-card p2-card)
                       , :p2 p2-rem-deck
                       }
                       (inc turn)
                )
                ;; p2 wins turn
                (recur { :p1 p1-rem-deck
                       , :p2 (conj p2-rem-deck p2-card p1-card)
                       }
                       (inc turn)
                )
            )
        )
    )
)


(defn score-game [decks] ; -> game score
    (let [winning-deck (val (second (sort-by #(count (val %)) decks)))]
        (apply
            +
            (map-indexed
                (fn [idx elem]
                    (* (inc idx) elem) ; idx is 0-based, we need 1-based
                )
                (reverse winning-deck) ; *bottom* card is 1, next is 2, etc
            )
        )
    )
)


(def sample-input
"Player 1:
9
2
6
3
1

Player 2:
5
8
4
7
10"
)

(def sample-decks (parse-input sample-input))
(println "SAMPLE")
(def sample-outcome (play sample-decks 1))
(println (str "sample score: " (score-game sample-outcome))) ; 306

(println)

(def input22 (slurp "input22.txt"))
(def input22-decks (parse-input input22))
(println "Part 1")
(def p1-outcome (play input22-decks 1))
(println (str "p1 score: " (score-game p1-outcome))) ; 33403
