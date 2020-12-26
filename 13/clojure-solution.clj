
(ns net.blergh.advent2020
)

(defn any-bus? [departure-time bus-schedule] ; -> bus-id or nil
    (if (empty? bus-schedule)
        nil
        (let [bus (first bus-schedule)]
            (if (= 0 (mod departure-time bus))
                bus
                (any-bus? departure-time (rest bus-schedule))
            )
        )
    )
)

(defn first-bus [earliest-departure bus-schedule] ; -> [time bus-id]
    (loop [departure-time earliest-departure]
        (let [bus (any-bus? departure-time bus-schedule)]
            (if (not (nil? bus))
                [departure-time bus]
                (recur (+ departure-time 1))
            )
        )
    )
)

;sample
;(println (first-bus 939 [7 13 59 31 19]))
;answer (944 - 939) * 59 -> 295

;part1
;(println (first-bus 1000677 [29 41 661 13 17 23 521 37 19]))
;answer (1000684 - 1000677) * 23 -> 161



;part2...

(defn check-time [atime bus-offsets] ; -> true/false
    (every? 
        true?
        (map
            (fn [bus-offset]
                (let [ [offset bus-id] bus-offset
                     , offset-time (+ atime offset)
                     ]
                    (= 0 (mod offset-time bus-id))
                )
            )
            (seq bus-offsets)
        )
    )
)

(defn search-for-bus-sequence [time-seq bus-offsets] ; -> atime
    (loop [inner-time-seq time-seq]
        (let [atime (first inner-time-seq)]
            (if (nil? atime)
                nil
                (do
                    (println (str "checking " atime))
                    (if (check-time atime bus-offsets)
                        atime
                        (recur (rest inner-time-seq))
                    )
                )
            )
        )
    )
)

;including the above linear-search code because it "works" but is far too slow
;to ever find the answer

;credit to this answer I found on reddit https://pastebin.com/jHpRYhzc
;for making the right technique 'click' for me

(defn search-with-periodicity [time period bus-offsets]
    (if (empty? bus-offsets)
        time
        (let [ [offset bus-id] (first bus-offsets)
             , offset-time (+ time offset)
             , next-bus-is-there (= 0 (mod offset-time bus-id))
             ]
            (println (str "checking " time " (period is " period ")"))
            (if next-bus-is-there
                ; call recursively at same time, period modified, rest of offsets
                (search-with-periodicity time (* period bus-id) (rest bus-offsets))
                ; call recursively with time incremented, same period, same offsets
                (search-with-periodicity (+ time period) period bus-offsets)
            )
        )
    )
)


(def sample-offsets 
    [ [0 7]
    , [1 13]
    , [4 59]
    , [6 31]
    , [7 19]
    ]
)

(def sample-offsets-short
    [ [0 17]
    , [2 13]
    , [3 19]
    ]
)

(def sample-result (search-with-periodicity 0 7 (rest sample-offsets)))
(println (str "found sequence at " sample-result))


(def input-offsets
    [ [ 0  29]
    , [19  41]
    , [29 661]
    , [42  13]
    , [43  17]
    , [52  23]
    , [60 521]
    , [66  37]
    , [79  19]
    ]
)

(def input-result (search-with-periodicity 0 29 (rest input-offsets)))
(println (str "found sequence at " input-result))
;answer=213890632230818 (or 213,890,632,230,818)
