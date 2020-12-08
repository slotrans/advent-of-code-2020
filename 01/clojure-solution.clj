(def puzzle-input-from-file (map edn/read-string (str/split (slurp "input01") #"\s")))

(defn sums-to-target?
    [x]
    (= 2020 (apply + x))
)

(defn print-answer
    (
        [i j]
        (println (str i " + " j " = 2020; " i " * " j " = " (* i j)))
    )
    (
        [i j k]
        (println (str i " + " j " + " k " = 2020; " i " * " j " * " k " = " (* i j k)))
    )
)

(comment
(println "iterative solution, part 1")
(let [puzzle-input puzzle-input-from-file]
    (doseq [i puzzle-input]
        (doseq [j puzzle-input]
            (if (sums-to-target? [i j])
                (print-answer i j)
            )
        )
    )
)

(println "iterative solution, part 2")
(let [puzzle-input puzzle-input-from-file]
    (doseq [i puzzle-input]
        (doseq [j puzzle-input]
            (doseq [k puzzle-input]
                (if (sums-to-target? [i j k])
                    (print-answer i j k)
                )
            )
        )
    )
)
)

(println "recursive solution")
(defn find-puzzle-pair
    [possible-value possible-matches]
    (if (empty? possible-matches)
        nil ; no match found
        (let [i (first possible-matches)]
            (if (sums-to-target? [possible-value i])
                [possible-value i]
                (find-puzzle-pair possible-value (rest possible-matches))
            )
        )
    )
)

(defn find-puzzle-triple
    [possible-i possible-j possible-others]
    (if (empty? possible-others)
        nil ; no match found
        (let [k (first possible-others)]
            (if (sums-to-target? [possible-i possible-j k])
                [possible-i possible-j k]
                (find-puzzle-triple possible-i possible-j (rest possible-others))
            )
        )
    )
)

(comment
(loop
    [ j (first puzzle-input-from-file)
    , remaining (rest puzzle-input-from-file)
    ]
    (if (empty? remaining)
        (println "no solution found")
        (let [result (find-puzzle-pair j remaining)]
            (if result
                (apply print-answer result)
                (recur
                    (first remaining)
                    (rest remaining)
                )
            )
        )
    )
)
)
; not super happy with this ^^^^^^^^^^^

(println "part 1")
(def result-seq1
    (map 
        #(find-puzzle-pair % puzzle-input-from-file) 
        puzzle-input-from-file
    )
)
(def result1 
    (first (filter some? result-seq1))
)
(apply print-answer result1)


(println "part 2")
(defn inner-map
    [fixed-val value-vector]
    (map
        #(find-puzzle-triple fixed-val % value-vector)
        value-vector
    )
)
(def result-seq2
    (apply 
        concat
        (map
            #(inner-map % puzzle-input-from-file)
            puzzle-input-from-file
        )
    )
)
(def result2
    (first (filter some? result-seq2))
)
(apply print-answer result2)

; still not really happy with this but whatever
