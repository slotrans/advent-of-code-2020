(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
    )
)


; AND-ing with 0 forces a bit to 0 / AND-ing a bit with 1 leaves it unchanged
;  OR-ing with 1 forces a bit to 1 /  OR-ing a bit with 0 leaves it unchanged

(defn parse-mask [mask-line]
    (let [ [_ mask-input] (str/split mask-line #" = ")
         ]
        { :and-mask (edn/read-string (str "2r" (str/replace mask-input "X" "1")))
        , :or-mask  (edn/read-string (str "2r" (str/replace mask-input "X" "0")))
        }
    )
)

(defn apply-mask [masks x]
    (let [{:keys [and-mask or-mask]} masks
         ]
        (->> x
            (bit-and and-mask ,,,)
            (bit-or or-mask ,,,)
        )
    )
)

(defn run-instruction [instruction memory masks]
    ; instruction "mem[123] = 456" becomes
    ;   mem-addr = 123
    ;   value = 456
    (let [ [leftside rightside] (str/split instruction #" = ")
         ;, mem-addr (edn/read-string (subs leftside 4 (- (count leftside) 1)))
         , mem-addr (edn/read-string (apply str (drop 4 (drop-last 1 leftside)))) ; does the same as above but maybe clearer? idk
         , value (edn/read-string rightside)
         , masked-value (apply-mask masks value)
         ]
        (assoc memory mem-addr masked-value)
    )
)

(defn run-program [all-program-lines]
    (loop [ program-lines all-program-lines
          , memory {}
          , masks {} ; first input line is a mask so safe defaults not necessary
          ]
        (let [line (first program-lines)]
            (if (nil? line)
                memory
                (do
                    ;(println line)
                    (if (str/starts-with? line "mask")
                        (recur (rest program-lines) memory (parse-mask line))
                        (recur (rest program-lines) (run-instruction line memory masks) masks)
                    )  
                ) 
            )
        )
    )
)


(def sample-input
"mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X
mem[8] = 11
mem[7] = 101
mem[8] = 0"
)

(def sample-program-lines (str/split sample-input #"\n"))

(def sample-memory (run-program sample-program-lines))

(def sample-sum (reduce + (vals sample-memory)))

(println "sample sum is" sample-sum)


(def input14 (slurp "input14.txt"))
(def input14-lines (str/split input14 #"\n"))
(def part1-memory (run-program input14-lines))
(def part1-sum (reduce + (vals part1-memory)))
(println "part1 sum is" part1-sum)
