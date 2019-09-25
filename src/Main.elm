module Main exposing (main)

import Browser
import Browser.Events as Events
import Element exposing (Element, column, el, px, row, text)
import Element.Background as Background
import Html exposing (Html, div)
import Html.Attributes exposing (class, classList, style)
import Html.Events exposing (on, onMouseLeave, onMouseUp)
import Json.Decode as Decode exposing (Decoder)
import Return
import Theme



-- model


type alias Point =
    { x : Int, y : Int }


type Tool
    = SelectionTool (Maybe Int)
    | BoxTool (Maybe BoxDefinition)


type alias Selection =
    { index : Int }


type alias BoxDefinition =
    { startPoint : Point, endPoint : Point }


type alias Model =
    { tool : Tool
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
    { boxes = []
    , tool = SelectionTool Nothing
    }


init : () -> ( Model, Cmd msg )
init _ =
    Return.singleton initialState


subscriptions : Model -> Sub Msg
subscriptions _ =
    Events.onKeyUp (Decode.map KeyPress keyboardEventKey)


keyboardEventKey : Decoder String
keyboardEventKey =
    Decode.field "key" Decode.string


boxFromDefinition : BoxDefinition -> Box
boxFromDefinition { startPoint, endPoint } =
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
    | MouseDown Point
    | MouseMove Point
    | MouseUp Point
    | KeyPress String


update : Msg -> Model -> ( Model, Cmd msg )
update msg model =
    case msg of
        MouseDown point ->
            case model.tool of
                BoxTool _ ->
                    Return.singleton { model | tool = BoxTool (Just { startPoint = point, endPoint = point }) }

                SelectionTool _ ->
                    Return.singleton model

        MouseMove point ->
            case model.tool of
                BoxTool (Just boxDefinition) ->
                    Return.singleton { model | tool = BoxTool (Just { boxDefinition | endPoint = point }) }

                _ ->
                    Return.singleton model

        MouseUp point ->
            case model.tool of
                BoxTool (Just boxDefinition) ->
                    let
                        newBox =
                            boxFromDefinition boxDefinition
                    in
                    Return.singleton
                        { model
                            | tool = SelectionTool Nothing
                            , boxes =
                                model.boxes
                                    -- only add box if it is at least 1 x 1 pixels
                                    ++ (if newBox.width > 0 && newBox.height > 0 then
                                            [ newBox ]

                                        else
                                            []
                                       )
                        }

                _ ->
                    Return.singleton model

        KeyPress key ->
            Return.singleton
                (case String.toLower key of
                    "b" ->
                        { model | tool = BoxTool Nothing }

                    "escape" ->
                        { model | tool = SelectionTool Nothing }

                    _ ->
                        model
                )

        NoOp ->
            Return.singleton model



-- view


sidebar : Element Msg
sidebar =
    column
        [ Background.color Theme.subtleLightColor
        , Element.width (300 |> px)
        , Element.height Element.fill
        ]
        [ text "press b to create a box"
        ]


canvas : List Box -> Tool -> Element.Element Msg
canvas boxes tool =
    el
        [ Element.width Element.fill
        , Element.height Element.fill
        ]
        (Element.html
            (div
                [ class "canvas"
                , style "cursor"
                    (case tool of
                        BoxTool _ ->
                            "crosshair"

                        _ ->
                            "inherit"
                    )
                , on "mousedown" (Decode.map MouseDown mousePosition)
                , on "mousemove" (Decode.map MouseMove mousePosition)
                , on "mouseup" (Decode.map MouseUp mousePosition)

                -- TODO: handle this better
                , on "mouseleave" (Decode.map MouseUp mousePosition)
                ]
                (List.map boxView boxes
                    ++ toolPreview tool
                )
            )
        )


toolPreview : Tool -> List (Html msg)
toolPreview tool =
    case tool of
        BoxTool (Just boxDefinition) ->
            let
                box =
                    boxFromDefinition boxDefinition
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

        _ ->
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


view model =
    Element.layout
        []
        (row
            [ Element.width Element.fill
            , Element.height Element.fill
            ]
            [ sidebar
            , canvas model.boxes model.tool
            ]
        )


main =
    Browser.element
        { init = init
        , update = update
        , view = view
        , subscriptions = subscriptions
        }
