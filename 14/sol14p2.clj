(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
    )
)


(defn parse-mask [mask-line]
    (let [ [_ mask-input] (str/split mask-line #" = ")
         ]
        mask-input
    )
)

(defn to-36-bit-binary [x]
    (let [ binary-unpadded (Integer/toString x 2)
         , length (count binary-unpadded)
         , padding (apply str (repeat (- 36 length) "0"))
         ]
         (str padding binary-unpadded)
    )
)


; If the bitmask bit is 0, the corresponding memory address bit is unchanged.
; If the bitmask bit is 1, the corresponding memory address bit is overwritten with 1.
; If the bitmask bit is X, the corresponding memory address bit is floating.
(defn apply-mask [mem-addr mask]
    (let [ mem-addr-string (to-36-bit-binary mem-addr)
         ]
        (apply
            str
            (for [i (range 0 36)]
                (if (= \0 (nth mask i)) 
                    (nth mem-addr-string i)
                    (nth mask i) ; \1 or \X
                )
            )
        )
    )
)


(defn expand-floating-bits [bit-vec]
    (loop [ bits-so-far []
          , this-bit (first bit-vec)
          , bits-remaining (rest bit-vec)
          ]
        (if (nil? this-bit)
            (apply str bits-so-far) ;convert back to string once we've reached the end
            (if (= \X this-bit)
                (let [ with-zero (expand-floating-bits (concat bits-so-far [\0] bits-remaining)) 
                     , with-one  (expand-floating-bits (concat bits-so-far [\1] bits-remaining))
                     ]
                    [with-zero with-one]
                )
                (recur (conj bits-so-far this-bit)
                       (first bits-remaining)
                       (rest bits-remaining)
                )
            )
        )
    )
)


(defn run-instruction [instruction memory mask]
    ; instruction "mem[123] = 456" becomes
    ;   mem-addr = 123
    ;   value = 456
    (let [ [leftside rightside] (str/split instruction #" = ")
         , value (edn/read-string rightside)
         , mem-addr (edn/read-string (apply str (drop 4 (drop-last 1 leftside))))
         , masked-mem-addr (apply-mask mem-addr mask)
         , mem-addr-list (flatten (expand-floating-bits masked-mem-addr))
         ]
        (into
            memory
            (for [addr mem-addr-list]
                {addr value}
            )
        )
    )
)


;almost identical to part1
(defn run-program [all-program-lines]
    (loop [ program-lines all-program-lines
          , memory {}
          , mask "" ; first input line is a mask so safe defaults not necessary
          ]
        (let [line (first program-lines)]
            (if (nil? line)
                memory
                (do
                    ;(println line)
                    (if (str/starts-with? line "mask")
                        (recur (rest program-lines) memory (parse-mask line))
                        (recur (rest program-lines) (run-instruction line memory mask) mask)
                    )  
                ) 
            )
        )
    )
)



(def sample-input
"mask = 000000000000000000000000000000X1001X
mem[42] = 100
mask = 00000000000000000000000000000000X0XX
mem[26] = 1"
)

(def sample-program-lines (str/split sample-input #"\n"))

(def sample-memory (run-program sample-program-lines))

(def sample-sum (reduce + (vals sample-memory)))

(println "sample sum is" sample-sum)


(def input14 (slurp "input14.txt"))
(def input14-lines (str/split input14 #"\n"))
(def part2-memory (run-program input14-lines))
(def part2-sum (reduce + (vals part2-memory)))
(println "part2 sum is" part2-sum)
