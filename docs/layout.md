# UI library by example

inspired by [Elm UI](https://package.elm-lang.org/packages/mdgriffith/elm-ui/)


## Layout

### Units
```clojure
(px 10)
(fraction 1)
```

### Size

default: shrink

```clojure
{:width :fill}
{:width :shrink}
{:width (px 100)}
{:width (fraction 100)}
{:width {:value :fill :max (px 100) :min (px 200)}} 
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
{:padding (px 20)}
{:padding [(px 20) (px 20)]}
{:padding [(px 20) (px 20) (px 20) (px 20)]}

{:evenSpacing :true} ; sets padding and gap evenly, overrides padding and gap

;; row / column specific
{:gap (px 10)} 
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






