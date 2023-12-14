;; namespace for global variables 
(ns srcerizder-site.global)

;; Public Dirs
(def publics (atom "resources/public/"))
(def public-styles (atom "resources/public/styles/"))

;; Private Data
(def edn-docs (atom "resources/private/hiccup"))
(def edn-styles (atom "resources/private/garden"))

;; Export Dirs
(def export-dir (atom "./dist"))
(def export-style-dir (atom "./dist/styles"))
