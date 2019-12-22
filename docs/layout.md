# UI library by example

inspired by [Elm UI](https://package.elm-lang.org/packages/mdgriffith/elm-ui/)


## Layout

### Size

default: shrink

```clojure
{:width :fill}
{:width :shrink}
{:width [100 :px]}
{:width [100 :part]}
{:width {:value :fill :max [100 :px] :min [200 :px]}} 
```

### Overflow

default: 

```clojure
{:overflow :clip}
{:overflow :scroll}
{:overflow {:x :scroll}}
```

### Spacing 

```clojure
{:padding [20 :px]}
{:padding [[20 :px] [10 :px]]}
{:padding [[20 :px] [20 :px] [10 :px] [10 :px]]}

;; row / column specific
{:gap [10 :px]}
{:gap :auto}
```


### Alignment

```clojure
{:align {:x :start :y :end}}
{:align :center }
{:align :start }
```

- Reason for using more generic terminology
  - Only visible in bottom layer, can be translated in ui to left/right top/button 
  - More flexible: vertical stack could be turned into a horizontal stack


## Styling

This should be extended in the future. Right now this is just an escape hatch to add some styling. The list of
 supported properties should be kept to a minimum.


- background
- border


```clojure
{:background "red"}
{:background "rgb(255, 255, 0)"}
{:border "1px solid red"}
```

# Font

```clojure
{:font {
  :color "red" 
  :size 5 
  :family "Helvetica, sans-serif"
  :weight "bold"
  :style :underline
}}
```






