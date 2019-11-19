#dev setup


## lein

install

```shell script
 brew install leiningen
```

remove compiled files can be configured in `project.clj` with `:clean-targets` property

```shell script
lein clean
```

### Plugins

## cljs build

build ClojureScript projects

```shell script
lein cljsbuild auto
```
