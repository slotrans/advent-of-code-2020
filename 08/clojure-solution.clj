;For example, consider the following program:
;
;nop +0
;acc +1
;jmp +4
;acc +3
;jmp -3
;acc -99
;acc +1
;jmp -4
;acc +6

;First, the nop +0 does nothing. Then, the accumulator is increased from 0 to 1 (acc +1) 
;and jmp +4 sets the next instruction to the other acc +1 near the bottom. After it 
;increases the accumulator from 1 to 2, jmp -4 executes, setting the next instruction to 
;the only acc +3. It sets the accumulator to 5, and jmp -3 causes the program to continue 
;back at the first acc +1.

;This is an infinite loop: with this sequence of jumps, the program will run forever. 
;The moment the program tries to run any instruction a second time, you know it will never terminate.

;Immediately before the program would run an instruction a second time, 
;the value in the accumulator is 5.

;Run your copy of the boot code. Immediately before any instruction is executed a second time, 
;what value is in the accumulator?


(ns net.blergh.advent2020
    (:require [clojure.string :as str]
              [clojure.edn :as edn]
    )
)

(def input08 (slurp "input08"))

(def input-lines (str/split-lines input08))

(def input-code 
    (vec
        (for [line input-lines]
            (vec (str/split line #" "))
        )
    )
)

(def sample-input
"nop +0
acc +1
jmp +4
acc +3
jmp -3
acc -99
acc +1
jmp -4
acc +6"
)
(def sample-code
    (vec
        (for [line (str/split-lines sample-input)]
            (vec (str/split line #" "))
        )
    )
)
;(println code)


(defn execute-until-repeat [code instr-num accumulator already-run-instr-nums]
    (if (>= instr-num (count code))
        ["terminated!" accumulator]
        (if (contains? already-run-instr-nums instr-num)
            ["looped!" accumulator]
            (let [ [instruction argument-string] (get code instr-num)
                 , argument (edn/read-string argument-string)
                 ]
                 ;(println (str "instruction=" instruction ", argument=" argument ", accumulator=" accumulator))
                 (cond
                     (= instruction "acc")  (execute-until-repeat 
                                                code
                                                (+ instr-num 1)
                                                (+ accumulator argument)
                                                (conj already-run-instr-nums instr-num)
                                            )
                     (= instruction "jmp")  (execute-until-repeat
                                                code
                                                (+ instr-num argument)
                                                accumulator
                                                (conj already-run-instr-nums instr-num)
                                            )
                     (= instruction "nop")  (execute-until-repeat
                                                code
                                                (+ instr-num 1)
                                                accumulator
                                                (conj already-run-instr-nums instr-num)
                                            )
                     :else (throw (Exception. "UNKNOWN INSTRUCTION"))
                 )
            )
        )
    )
)

(def sample-result (execute-until-repeat sample-code 0 0 #{}))
(println (str "(sample answer) program " (first sample-result) ", accumulator at first repeat = " (second sample-result)))
;answer=5

(def p1-result (execute-until-repeat input-code 0 0 #{}))
(println (str "(p1 answer) program " (first p1-result) ", accumulator at first repeat = " (second p1-result)))
;answer=2034


(def code-to-patch input-code)
(doseq [ [line-num [inst arg]] (map-indexed vector code-to-patch)]    
    (cond
        (= inst "acc")
            (println (str line-num ": (skipping acc)"))
        (= inst "nop")
            (do
                (print (str line-num ": swapping nop->jmp | "))
                (let [ modified-line ["jmp" arg]
                     , modified-code (assoc code-to-patch line-num modified-line)
                     , p2-test-result (execute-until-repeat modified-code 0 0 #{})
                     , [stop-type acc-value] p2-test-result
                     ]
                    (println (str "program " stop-type ", accumulator = " acc-value))
                )
            )
        (= inst "jmp")
            (do
                (print (str line-num ": swapping jmp->nop | "))
                (let [ modified-line ["nop" arg]
                     , modified-code (assoc code-to-patch line-num modified-line)
                     , p2-test-result (execute-until-repeat modified-code 0 0 #{})
                     , [stop-type acc-value] p2-test-result
                     ]
                    (println (str "program " stop-type ", accumulator = " acc-value))
                )
            )
    )
)
;solution at index 328, accumulator at termination = 672

