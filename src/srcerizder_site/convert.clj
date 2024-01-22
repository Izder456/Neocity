(ns srcerizder-site.convert
  (:require
   [garden.core :as garden]
   [hiccup2.core :as hiccup2]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.string :as string]
   [srcerizder-site.global :as global]
   [srcerizder-site.os :as os]))

(defn- load-edn [filename]
  (edn/read-string (slurp filename)))

(defn- load-and-generate-filename
  "Helper function for loading and generating filenames"
  [edn-filename base-dir ext]
  (let [base-filename (-> edn-filename
                          (os/basename)
                          (string/replace #"\.edn$" ""))
        filename (os/path-append base-dir (str base-filename ext))
        edn-data (load-edn edn-filename)]
    [filename edn-data]))

(defn- hiccup-to-html
  "Hiccup EDN data to html conversion"
  [edn-filename]
  (let [[html-filename hiccup-data] (load-and-generate-filename edn-filename @global/publics ".html")]
    (spit html-filename (str (hiccup2/html hiccup-data)))))

(defn- garden-to-css
  "Garden EDN data to css conversion"
  [edn-stylename]
  (let [[css-filename garden-data] (load-and-generate-filename edn-stylename @global/public-styles ".css")]
    (spit css-filename (str (garden/css garden-data)))))

(defn- process-files
  "Helper function for file processing"
  [edn-directory process-fn]
  (let [edn-files (file-seq (io/file edn-directory))]
    (doseq [edn-file edn-files
            :when (string/ends-with? edn-file ".edn")]
      (process-fn edn-file))))

(defn all-to-html [edn-directory]
  (process-files edn-directory hiccup-to-html))

(defn all-to-css [edn-directory]
  (process-files edn-directory garden-to-css))
