(ns htmx-app.services.todo-item-test
  (:require [clojure.test                :refer [deftest is testing]]
            [htmx-app.services.todo-item :as svc]))

(deftest validate-title
  (testing "valid titles pass"
    (is (nil? (svc/validate-title "Buy milk")))
    (is (nil? (svc/validate-title "x"))))

  (testing "blank titles are rejected"
    (is (some? (svc/validate-title "")))
    (is (some? (svc/validate-title "   "))))

  (testing "titles over 200 chars are rejected"
    (is (some? (svc/validate-title (apply str (repeat 201 "a")))))))
