# Changelog

## [2.1.0](https://github.com/madgeek-arc/authorization/compare/v2.0.0...v2.1.0) (2026-11-02)

### Features

* add group-aware overloads to Authorization interface with default backward-compatible implementations ([9066fa9](https://github.com/madgeek-arc/authorization/commit/9066fa9ace5aef0016907c24a470714d97ed1b15))

### Bug Fixes

* annotate with Transactional using the correct transaction manager ([3f4947d](https://github.com/madgeek-arc/authorization/commit/3f4947d4e9c11bd7dca98935b0b50ad09f8dd904))
* enforce unique permission tuples and index action/object lookups ([84fb35d](https://github.com/madgeek-arc/authorization/commit/84fb35d35d9aa5fdac8e0f755295d8ee8e648401))
* keep the host's default data source and JPA setup intact ([b45e1ea](https://github.com/madgeek-arc/authorization/commit/b45e1eabe72c0defa5b96d674f52d867b41913a1))
* override equals/hashCode and remove setters to keep hashCode stable when instances are held in Sets ([fc1f9d5](https://github.com/madgeek-arc/authorization/commit/fc1f9d538cc728aa9ca2e62a175766946d993505))
* Replace @ConditionalOnClass with @ConditionalOnMissingBean(Authorization.class) to enable downstream apps to supply their own implementation ([9754906](https://github.com/madgeek-arc/authorization/commit/975490649eff3ff18c1db4969215ce096769a5b6))
* stop shipping application.properties in the library jar ([fb09096](https://github.com/madgeek-arc/authorization/commit/fb0909618c4cfa015a72c92326816ba41fe6d760))

## 2.0.0 (2025-02-11)

### ⚠ BREAKING CHANGES

* Change fully qualified package names

### Bug Fixes

* Add datasource properties qualifier ([ab570bd](https://github.com/madgeek-arc/authorization/commit/ab570bd9ad3da84181a3fbdf00eb19605bda27c6))
* Change datasource configuration and properties ([c969659](https://github.com/madgeek-arc/authorization/commit/c969659eaf6cddd4e86f1d38a53c71236d73c293))

### Build System

* Change fully qualified package names ([a2942e7](https://github.com/madgeek-arc/authorization/commit/a2942e799c69b9ee9461abf2a44e4ea0caa8887e))
