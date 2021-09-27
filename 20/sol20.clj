(ns net.blergh.advent2020
    (:require [clojure.string :as str]
    )
)


(defn parse-tile-data [s] ; -> vector of lines, where each line is a vector of characters (pixels)
    (mapv
        vec
        (str/split-lines s)
    )
)


(defn parse-tile [s] ; -> map of tile-id -> pixels
    (let [ [tile-name tile-data] (str/split s #":\n")
         , [_ tile-id-as-str] (str/split tile-name #" ")
         , tile-id (Integer/parseInt tile-id-as-str)
         ]
        {tile-id (parse-tile-data tile-data)}
    )
)


(defn parse-input [s] ;-> map of tile-id -> pixels (across all tiles)
    (into
        {}
        (for [chunk (str/split s #"\n\n") :when (str/starts-with? chunk "Tile")]
            (parse-tile chunk)
        )
    )
)


(defn print-tile [tile]
    (let [[id tile-data] tile]
        (println (str "id=" id))
        (doseq [line tile-data]
            (println (apply str line))
        )
    )
)


(defn to-bit-string [s] 
    (-> s 
        (str/replace ,,, "#" "1") 
        (str/replace ,,, "." "0")
    )
)

(defn tile-edge-to-number [s]
    (edn/read-string (str "2r" (to-bit-string s)))
)


(defn get-edge [tile-data direction]
    (condp = direction
        :top    (apply str (get tile-data 0))
        :right  (apply str (for [line tile-data] (get line 9)))
        :bottom (apply str (get tile-data 9))
        :left   (apply str (for [line tile-data] (get line 0)))
    )
)


(defn rotate-tile [tile-data degrees] ; -> tile-data, rotated clockwise by degrees
    (condp = degrees
          0 tile-data
         90 (vec
                (for [pos (range 0 10)] ; for each position, left to right
                    (mapv
                        #(get % pos)
                        (reverse tile-data) ; take from that position, moving bottom to top
                    )
                )
            )
        180 (rotate-tile (rotate-tile tile-data 90) 90)
        270 (rotate-tile (rotate-tile (rotate-tile tile-data 90) 90) 90)
    )
)


(defn mirror-tile-horiz [tile-data] ; -> tile-data, mirrored horizontally
    (mapv
        #(reverse %)
        tile-data
    )
)


(defn mirror-tile-vert [tile-data] ; -> tile-data, mirrored vertically
    (mapv
        identity
        (reverse tile-data)
    )
)



(def sample-input (slurp "sample-input.txt"))
(def parsed-sample-input (parse-input sample-input))
(doseq [tile parsed-sample-input]
    ;(println tile)
    (print-tile tile)
)


