(ns htmx-app.repository.todo-item-test
  (:require [clojure.test                  :refer [deftest is use-fixtures testing]]
            [htmx-app.test-helpers         :refer [test-ds db-fixture]]
            [htmx-app.repository.user      :as user-repo]
            [htmx-app.repository.todo-list :as list-repo]
            [htmx-app.repository.todo-item :as repo]))

(use-fixtures :each db-fixture)

(defn- seed-list []
  (let [user (user-repo/create! @test-ds {:email "test@example.com" :password "pw" :role "user"})]
    (list-repo/create! @test-ds (:users/id user) "Test List")))

(deftest crud-todo-items
  (let [lst     (seed-list)
        list-id (:todo_lists/id lst)]

    (testing "create returns the new item"
      (let [item (repo/create! @test-ds list-id "Buy milk")]
        (is (= "Buy milk" (:todo_items/title item)))
        (is (= list-id (:todo_items/list_id item)))
        (is (false? (:todo_items/done item)))))

    (testing "find-all-by-list returns items in order"
      (repo/create! @test-ds list-id "Item A")
      (repo/create! @test-ds list-id "Item B")
      (let [items (repo/find-all-by-list @test-ds list-id)]
        (is (= 3 (count items)))))

    (testing "find-by-id returns nil for unknown id"
      (is (nil? (repo/find-by-id @test-ds 99999))))

    (testing "update-title! changes the title"
      (let [item    (repo/create! @test-ds list-id "Old Title")
            updated (repo/update-title! @test-ds (:todo_items/id item) "New Title")]
        (is (= "New Title" (:todo_items/title updated)))))

    (testing "toggle-done! flips done status"
      (let [item    (repo/create! @test-ds list-id "Toggle me")
            toggled (repo/toggle-done! @test-ds (:todo_items/id item))]
        (is (true? (:todo_items/done toggled)))
        (let [back (repo/toggle-done! @test-ds (:todo_items/id item))]
          (is (false? (:todo_items/done back))))))

    (testing "delete! removes the item"
      (let [item (repo/create! @test-ds list-id "To Delete")
            id   (:todo_items/id item)]
        (repo/delete! @test-ds id)
        (is (nil? (repo/find-by-id @test-ds id)))))))
