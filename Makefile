# Makefile for UP Java Parser

.PHONY: help
help: ## Show this help message
	@echo 'Usage: make [target]'
	@echo ''
	@echo 'Available targets:'
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  %-15s %s\n", $$1, $$2}' $(MAKEFILE_LIST)

.PHONY: test
test: ## Run tests
	mvn clean test

.PHONY: build
build: ## Build the project
	mvn clean package -DskipTests

.PHONY: package
package: ## Build and package (with tests)
	mvn clean package

.PHONY: clean
clean: ## Clean build artifacts
	mvn clean

.PHONY: install
install: ## Install to local Maven repository
	mvn clean install

.PHONY: test-ci
test-ci: ## Run CI tests locally using act (requires: brew install act)
	act --container-architecture linux/amd64 -j test
	act --container-architecture linux/amd64 -j build

.DEFAULT_GOAL := test
