
(ns net.blergh.advent2020
    (:require [clojure.set :as set]
              [clojure.string :as str]
    )
)

(def diff-list [1 3 1 1 1 3 1 1 3 1 3 3])

(defn do-the-thing [stream counter]
    (if (not (second stream))
        counter
        (let [ this-one (first stream)
             , next-one (second stream)
             ]
            (println (str "(" counter ") considering: " (vec stream)))
            (cond 
                (= this-one 1)
                    (cond
                        (= next-one 1)
                            (+ (do-the-thing (concat [2] (drop 2 stream)) (+ counter 1))
                               (do-the-thing (rest stream) counter)
                            )
                        (= next-one 2)
                            (+ (do-the-thing (concat [3] (drop 2 stream)) (+ counter 1))
                               (do-the-thing (rest stream) counter)
                            )
                        (= next-one 3)
                            (do-the-thing (rest stream) counter)
                    )
                (= this-one 2)
                    (cond
                        (= next-one 1)
                            (+ (do-the-thing (concat [3] (drop 2 stream)) (+ counter 1))
                               (do-the-thing (rest stream) counter)
                            )
                        (= next-one 2)
                            (do-the-thing (rest stream) counter)
                        (= next-one 3)
                            (do-the-thing (rest stream) counter)
                    )
                (= this-one 3)
                    (do-the-thing (rest stream) counter)
            )
        )
    )
)

;(do-the-thing diff-list 0)


(defn another-thing [stream pattern-set]
    (if (not (second stream))
        pattern-set
        (let [ this-one (first stream)
             , next-one (second stream)
             ]
            (println (str "(" (count pattern-set) ") considering: " (vec stream)))
            (cond 
                (= this-one 1)
                    (cond
                        (= next-one 1)
                            (let [modified-stream (vec (concat [2] (drop 2 stream)))]
                                (set/union (another-thing modified-stream (conj pattern-set modified-stream))
                                           (another-thing (rest stream) pattern-set)
                                )
                            )
                        (= next-one 2)
                            (let [modified-stream (vec (concat [3] (drop 2 stream)))]
                                (set/union (another-thing modified-stream (conj pattern-set modified-stream))
                                           (another-thing (rest stream) pattern-set)
                                )
                            )
                        (= next-one 3)
                            (another-thing (rest stream) pattern-set)
                    )
                (= this-one 2)
                    (cond
                        (= next-one 1)
                            (let [modified-stream (vec (concat [3] (drop 2 stream)))]
                                (set/union (another-thing modified-stream (conj pattern-set modified-stream))
                                           (another-thing (rest stream) pattern-set)
                                )
                            )
                        (= next-one 2)
                            (another-thing (rest stream) pattern-set)
                        (= next-one 3)
                            (another-thing (rest stream) pattern-set)
                    )
                (= this-one 3)
                    (another-thing (rest stream) pattern-set)
            )
        )
    )
)

;(another-thing diff-list #{diff-list})


(defn third-thing [stream pset depth]
    ;(println (str "(" depth ") entering fn with stream: " (vec stream)))
    (apply set/union
        (for [idx (range 0 (count stream))]
            (let [ cur (get stream idx)
                 , nxt (get stream (+ 1 idx))
                 ]
                ;(println (str "(" depth ") " stream))
                ;(println (str "(" depth ")  " (apply str (repeat idx "  ")) "^ ^"))
                (if (not nxt)
                    pset
                    (cond
                        (= [cur nxt] [1 1])
                            (do
                                ;(println "found 1 1")
                                (let [modified-stream (vec (concat (take idx stream) [2] (drop (+ 2 idx) stream)))]
                                    (third-thing modified-stream (conj pset modified-stream) (+ 1 depth))
                                )
                            )
                        (or (= [cur nxt] [2 1])
                            (= [cur nxt] [1 2])
                        )
                            (do
                                ;(println (str "found " cur " " nxt))
                                (let [modified-stream (vec (concat (take idx stream) [3] (drop (+ 2 idx) stream)))]
                                    (third-thing modified-stream (conj pset modified-stream) (+ 1 depth))
                                )
                            )
                        :else pset
                    )
                )
            )
        )
    )
)


(def res (third-thing diff-list #{diff-list} 0))
(println (str "result set (" (count res) ")"))
;(println res)
(doseq [i res]
    (println i)
)


;[1 2 3 4 7 8 9 10 11 14 17 18 19 20 23 24 25 28 31 32 33 34 35 38 39 42 45 46 47 48 49]
(def longer-diff-list [1 1 1 1 3 1 1 1 1 3 3 1 1 1 3 1 1 3 3 1 1 1 1 3 1 3 3 1 1 1 1])

(def longer-diff-list-as-str (str/join longer-diff-list))

; any sequence of at least two 1's/2's has permutations in it, everything else can be ignored
(def sub-chain-strings (re-seq #"[12]{2,}" longer-diff-list-as-str))
(def sub-chains 
    (for [i sub-chain-strings]
        (map 
            (fn [x] (Integer/parseInt (str x)))
            (vec i)
        )
    )
)

(println (str "longer chain: " longer-diff-list))
(doseq [sc sub-chains]
    (let [ sc-vec (vec sc)
         , solutions (third-thing sc-vec #{sc-vec} 0)
         , sol-cnt (count solutions)
         ]
        (println (str sc-vec "->" solutions))
    )
)
(def longer-diff-list-permutation-count
    (apply
        *
        (for [sc sub-chains]
            (let [ sc-vec (vec sc)
                 , solutions (third-thing sc-vec #{sc-vec} 0)
                 , sol-cnt (count solutions)
                 ]
                sol-cnt
            )        
        )
    )
)
(println (str "longer list permutations: " longer-diff-list-permutation-count))
