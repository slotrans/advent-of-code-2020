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



(def sample-input [3 8 9 1 2 5 4 6 7])

(def input23 [8 7 2 4 9 5 1 3 6])

