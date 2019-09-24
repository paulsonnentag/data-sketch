module Main exposing (main)

import Browser
import Element exposing (column, el, px, row)
import Element.Background as Background
import Html exposing (Html, div)
import Html.Attributes exposing (class, classList, style)
import Html.Events exposing (on, onMouseLeave, onMouseUp)
import Json.Decode as Decode exposing (Decoder)
import Theme



-- model


type alias Point =
    { x : Int, y : Int }


type MouseAction
    = None
    | CreateBox Point Point


type alias Model =
    { mouseAction : MouseAction
    , boxes : List Box
    }


type alias Box =
    { top : Int
    , left : Int
    , width : Int
    , height : Int
    }


initialState : Model
initialState =
    { mouseAction = None
    , boxes = []
    }


boxFromCorners : Point -> Point -> Box
boxFromCorners startPoint endPoint =
    let
        left =
            min startPoint.x endPoint.x

        right =
            max startPoint.x endPoint.x

        top =
            min startPoint.y endPoint.y

        bottom =
            max startPoint.y endPoint.y
    in
    { left = left
    , top = top
    , width =
        right - left
    , height =
        bottom - top
    }



-- update


type Msg
    = NoOp
    | StartCreateBox Point
    | UpdateMouseAction Point
    | CompleteMouseAction


update msg model =
    case msg of
        StartCreateBox startPoint ->
            { model | mouseAction = CreateBox startPoint startPoint }

        UpdateMouseAction endPoint ->
            case model.mouseAction of
                CreateBox startPoint _ ->
                    { model | mouseAction = CreateBox startPoint endPoint }

                None ->
                    model

        CompleteMouseAction ->
            case model.mouseAction of
                CreateBox startPoint endPoint ->
                    let
                        newBox =
                            boxFromCorners startPoint endPoint
                    in
                    { model
                        | mouseAction = None
                        , boxes =
                            model.boxes
                                -- only add box if it is at least 1 x 1 pixels
                                ++ (if newBox.width > 0 && newBox.height > 0 then
                                        [ newBox ]

                                    else
                                        []
                                   )
                    }

                None ->
                    model

        NoOp ->
            model



-- view


sidebar =
    column
        [ Background.color Theme.subtleLightColor
        , Element.width (300 |> px)
        , Element.height Element.fill
        ]
        []


canvas : List Box -> MouseAction -> Element.Element Msg
canvas boxes mouseAction =
    el
        [ Element.width Element.fill
        , Element.height Element.fill
        ]
        (Element.html
            (div
                [ class "canvas"
                , classList [ ( "disable-child-mouse-events", mouseAction /= None ) ]
                , style "cursor" "crosshair"
                , on "mousedown" (Decode.map StartCreateBox mousePosition)
                , on "mousemove" (Decode.map UpdateMouseAction mousePosition)
                , onMouseLeave CompleteMouseAction
                , onMouseUp CompleteMouseAction
                ]
                (List.map boxView boxes
                    ++ mouseActionPreview mouseAction
                )
            )
        )


mouseActionPreview : MouseAction -> List (Html msg)
mouseActionPreview mouseAction =
    case mouseAction of
        CreateBox startPoint endPoint ->
            let
                box =
                    boxFromCorners startPoint endPoint
            in
            [ div
                [ class "createBoxPreview"
                , style "width" (String.fromInt box.width ++ "px")
                , style "height" (String.fromInt box.height ++ "px")
                , style "top" (String.fromInt box.top ++ "px")
                , style "left" (String.fromInt box.left ++ "px")
                ]
                []
            ]

        None ->
            []


mousePosition : Decoder Point
mousePosition =
    Decode.map2 Point
        (Decode.field "offsetX" Decode.int)
        (Decode.field "offsetY" Decode.int)


boxView : Box -> Html Msg
boxView { top, left, width, height } =
    div
        [ class "box"
        , style "top" (String.fromInt top ++ "px")
        , style "left" (String.fromInt left ++ "px")
        , style "width" (String.fromInt width ++ "px")
        , style "height" (String.fromInt height ++ "px")
        , style "background-color" (Theme.colorToString Theme.highlightColor)
        ]
        []


view : Model -> Html Msg
view model =
    Element.layout
        []
        (row
            [ Element.width Element.fill
            , Element.height Element.fill
            ]
            [ sidebar
            , canvas model.boxes model.mouseAction
            ]
        )


main =
    Browser.sandbox { init = initialState, update = update, view = view }
