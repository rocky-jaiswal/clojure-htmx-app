(ns htmx-app.services.todo-list-test
  (:require [clojure.test                :refer [deftest is testing]]
            [htmx-app.services.todo-list :as svc]))

(deftest validate-name
  (testing "valid names pass"
    (is (nil? (svc/validate-name "Weekend Getaway")))
    (is (nil? (svc/validate-name "a"))))

  (testing "blank names are rejected"
    (is (some? (svc/validate-name "")))
    (is (some? (svc/validate-name "   "))))

  (testing "names over 100 chars are rejected"
    (is (some? (svc/validate-name (apply str (repeat 101 "a")))))))

(deftest owned-by?
  (let [lst {:todo_lists/user_id 42}]
    (testing "returns true when user_id matches"
      (is (true? (svc/owned-by? lst 42))))
    (testing "returns false when user_id differs"
      (is (false? (svc/owned-by? lst 99))))))
