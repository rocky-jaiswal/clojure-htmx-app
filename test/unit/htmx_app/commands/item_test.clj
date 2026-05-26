(ns htmx-app.commands.item-test
  (:require [clojure.test                :refer [deftest is testing]]
            [htmx-app.commands.item      :as cmd]
            [htmx-app.services.todo-list :as list-svc]
            [htmx-app.services.todo-item :as item-svc]))

(def ^:private user-id 1)
(def ^:private other-user-id 99)

(def ^:private sample-list
  {:todo_lists/id 10 :todo_lists/user_id user-id :todo_lists/name "My List"})

(def ^:private sample-item
  {:todo_items/id 20 :todo_items/list_id 10 :todo_items/title "Buy milk" :todo_items/done false})

(deftest add-item!
  (testing "blank title returns error"
    (with-redefs [list-svc/find-by-id (fn [_ _] sample-list)
                  list-svc/owned-by?  (fn [_ _] true)]
      (is (some? (:error (cmd/add-item! nil 10 user-id ""))))))

  (testing "returns forbidden for wrong user"
    (with-redefs [list-svc/find-by-id (fn [_ _] sample-list)
                  list-svc/owned-by?  (fn [_ _] false)]
      (is (= :forbidden (:error (cmd/add-item! nil 10 other-user-id "Buy milk"))))))

  (testing "creates item for owner"
    (with-redefs [list-svc/find-by-id (fn [_ _] sample-list)
                  list-svc/owned-by?  (fn [_ _] true)
                  item-svc/create!    (fn [_ _ _] sample-item)]
      (is (= sample-item (:ok (cmd/add-item! nil 10 user-id "Buy milk")))))))

(deftest toggle-item!
  (testing "returns forbidden for wrong user"
    (with-redefs [list-svc/find-by-id (fn [_ _] sample-list)
                  list-svc/owned-by?  (fn [_ _] false)
                  item-svc/find-by-id (fn [_ _] sample-item)]
      (is (= :forbidden (:error (cmd/toggle-item! nil 10 20 other-user-id))))))

  (testing "toggles item for owner"
    (with-redefs [list-svc/find-by-id  (fn [_ _] sample-list)
                  list-svc/owned-by?   (fn [_ _] true)
                  item-svc/find-by-id  (fn [_ _] sample-item)
                  item-svc/toggle-done! (fn [_ _] (assoc sample-item :todo_items/done true))]
      (is (true? (get-in (cmd/toggle-item! nil 10 20 user-id) [:ok :todo_items/done]))))))
