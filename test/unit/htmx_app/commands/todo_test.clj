(ns htmx-app.commands.todo-test
  (:require [clojure.test           :refer [deftest is testing]]
            [htmx-app.commands.todo :as cmd]
            [htmx-app.services.todo-list :as list-svc]
            [htmx-app.services.todo-item :as item-svc]))

(def ^:private user-id 1)
(def ^:private other-user-id 99)

(def ^:private sample-list
  {:todo_lists/id 10 :todo_lists/user_id user-id :todo_lists/name "My List"})

(def ^:private sample-item
  {:todo_items/id 20 :todo_items/list_id 10 :todo_items/title "Buy milk" :todo_items/done false})

(deftest create-list!
  (testing "blank name returns error"
    (is (some? (:error (cmd/create-list! nil user-id "")))))

  (testing "valid name creates list"
    (with-redefs [list-svc/create! (fn [_ _ _] sample-list)]
      (is (= sample-list (:ok (cmd/create-list! nil user-id "My List")))))))

(deftest get-list
  (testing "returns not-found when list does not exist"
    (with-redefs [list-svc/find-by-id (fn [_ _] nil)]
      (is (= :not-found (:error (cmd/get-list nil 10 user-id))))))

  (testing "returns forbidden when user does not own the list"
    (with-redefs [list-svc/find-by-id (fn [_ _] sample-list)
                  list-svc/owned-by?  (fn [_ _] false)]
      (is (= :forbidden (:error (cmd/get-list nil 10 other-user-id))))))

  (testing "returns list and items for the owner"
    (with-redefs [list-svc/find-by-id        (fn [_ _] sample-list)
                  list-svc/owned-by?          (fn [_ _] true)
                  item-svc/find-all-by-list   (fn [_ _] [sample-item])]
      (let [result (cmd/get-list nil 10 user-id)]
        (is (= sample-list (get-in result [:ok :list])))
        (is (= [sample-item] (get-in result [:ok :items])))))))

(deftest delete-list!
  (testing "returns not-found for missing list"
    (with-redefs [list-svc/find-by-id (fn [_ _] nil)]
      (is (= :not-found (:error (cmd/delete-list! nil 10 user-id))))))

  (testing "returns forbidden for wrong user"
    (with-redefs [list-svc/find-by-id (fn [_ _] sample-list)
                  list-svc/owned-by?  (fn [_ _] false)]
      (is (= :forbidden (:error (cmd/delete-list! nil 10 other-user-id))))))

  (testing "deletes and returns ok for owner"
    (with-redefs [list-svc/find-by-id (fn [_ _] sample-list)
                  list-svc/owned-by?  (fn [_ _] true)
                  list-svc/delete!    (fn [_ _] nil)]
      (is (true? (:ok (cmd/delete-list! nil 10 user-id)))))))

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
