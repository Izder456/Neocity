(ns srcerizder-site.os)

(defn basename
"Returns the filename from a given path."
[filename]
(let [filename-str (str filename)]
(subs filename-str (inc (clojure.string/last-index-of filename-str "/")) (count filename-str))))

(defn dirname
"Returns the directory containing the specified file."
[filename]
(subs filename 0 (inc (clojure.string/last-index-of filename "/"))))

(defn path-append [& paths]
(-> paths
(#(clojure.string/join "/" %))
(clojure.string/replace ,  #"[\\/]+" "/")))
