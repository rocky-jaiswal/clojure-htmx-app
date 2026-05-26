.PHONY: repl test test-unit test-integration lint fmt fmt-check build

repl:
	clj -M:dev -m nrepl.cmdline --middleware "[cider.nrepl/cider-middleware]"

test-unit:
	clj -M:test unit

test-integration:
	clj -M:test integration

test:
	clj -M:test

lint:
	clj -M:lint

fmt:
	clj -M:fmt fix src test

fmt-check:
	clj -M:fmt check src test

build:
	clj -T:build uber
