(ns htmx-app.repository.todo-list-test
  (:require [clojure.test                  :refer [deftest is use-fixtures testing]]
            [htmx-app.test-helpers         :refer [test-ds db-fixture]]
            [htmx-app.repository.user      :as user-repo]
            [htmx-app.repository.todo-list :as repo]))

(use-fixtures :each db-fixture)

(defn- seed-user []
  (user-repo/create! @test-ds {:email "test@example.com" :password "pw" :role "user"}))

(deftest crud-todo-lists
  (let [user    (seed-user)
        user-id (:users/id user)]

    (testing "create returns the new list"
      (let [lst (repo/create! @test-ds user-id "Weekend Getaway")]
        (is (= "Weekend Getaway" (:todo_lists/name lst)))
        (is (= user-id (:todo_lists/user_id lst)))
        (is (some? (:todo_lists/id lst)))))

    (testing "find-all-by-user returns only that user's lists"
      (repo/create! @test-ds user-id "List A")
      (repo/create! @test-ds user-id "List B")
      (let [lists (repo/find-all-by-user @test-ds user-id)]
        (is (= 3 (count lists)))
        (is (every? #(= user-id (:todo_lists/user_id %)) lists))))

    (testing "find-by-id returns the correct list"
      (let [lst    (repo/create! @test-ds user-id "Findable")
            found  (repo/find-by-id @test-ds (:todo_lists/id lst))]
        (is (= "Findable" (:todo_lists/name found)))))

    (testing "find-by-id returns nil for unknown id"
      (is (nil? (repo/find-by-id @test-ds 99999))))

    (testing "update-name! changes the name"
      (let [lst     (repo/create! @test-ds user-id "Old Name")
            updated (repo/update-name! @test-ds (:todo_lists/id lst) "New Name")]
        (is (= "New Name" (:todo_lists/name updated)))))

    (testing "delete! removes the list"
      (let [lst (repo/create! @test-ds user-id "To Delete")
            id  (:todo_lists/id lst)]
        (repo/delete! @test-ds id)
        (is (nil? (repo/find-by-id @test-ds id)))))))
