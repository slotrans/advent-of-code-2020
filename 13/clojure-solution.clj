
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
(println (first-bus 1000677 [29 41 661 13 17 23 521 37 19]))
;answer (1000684 - 1000677) * 23 -> 161

