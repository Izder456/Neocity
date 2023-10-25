(ns srcerizder-site.os)

(defn basename
  "Returns the filename from a given path."
  [filename]
  (subs filename (inc (clojure.string/last-index-of filename "/")) (count filename)))

(defn dirname
  "Returns the directory containing the specified file."
  [filename]
  (subs filename 0 (inc (clojure.string/last-index-of filename "/"))))

(defn path-append [& paths]
  (-> paths
      (#(clojure.string/join "/" %))
      (clojure.string/replace ,  #"[\\/]+" "/")))
