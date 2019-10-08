module Main exposing (main)

import Browser
import Browser.Events as Events
import Element exposing (Element, column, el, px, row)
import Element.Background as Background
import Element.Border as Border
import Element.Font as Font
import Html exposing (Html, div)
import Html.Attributes exposing (class, classList, id, style)
import Html.Events exposing (on)
import Json.Decode as Decode exposing (Decoder)
import Palette
import Return



-- model


type alias Point =
    { x : Int, y : Int }


type Tool
    = SelectionTool Selection
    | BoxTool (Maybe BoxDefinition)


type alias Selection =
    { selectedIndex : Maybe Int, mouseDownEvent : Maybe MouseEvent }


initialSelection : Selection
initialSelection =
    { selectedIndex = Nothing, mouseDownEvent = Nothing }


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


type alias MouseEvent =
    { position : Point
    , positionOnTarget : Point
    , target : Maybe Int
    }


initialState : Model
initialState =
    { boxes = []
    , tool = SelectionTool initialSelection
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
    | MouseDown MouseEvent
    | MouseMove MouseEvent
    | MouseUp MouseEvent
    | KeyPress String


update : Msg -> Model -> ( Model, Cmd msg )
update msg model =
    case msg of
        MouseDown event ->
            let
                { position, target } =
                    event
            in
            case model.tool of
                BoxTool _ ->
                    Return.singleton { model | tool = BoxTool (Just { startPoint = position, endPoint = position }) }

                SelectionTool _ ->
                    Return.singleton
                        { model
                            | tool =
                                SelectionTool
                                    { selectedIndex = target
                                    , mouseDownEvent = Just event
                                    }
                        }

        MouseMove { position, target } ->
            case model.tool of
                BoxTool (Just boxDefinition) ->
                    Return.singleton { model | tool = BoxTool (Just { boxDefinition | endPoint = position }) }

                SelectionTool { selectedIndex, mouseDownEvent } ->
                    case ( selectedIndex, mouseDownEvent ) of
                        ( Just index, Just event ) ->
                            if selectedIndex == event.target then
                                Return.singleton
                                    { model
                                        | boxes =
                                            List.indexedMap
                                                (\boxIndex box ->
                                                    if boxIndex == index then
                                                        { box
                                                            | left = position.x - event.positionOnTarget.x
                                                            , top = position.y - event.positionOnTarget.y
                                                        }

                                                    else
                                                        box
                                                )
                                                model.boxes
                                    }

                            else
                                Return.singleton model

                        _ ->
                            Return.singleton model

                _ ->
                    Return.singleton model

        MouseUp { position, target } ->
            case model.tool of
                BoxTool (Just boxDefinition) ->
                    let
                        newBox =
                            boxFromDefinition boxDefinition
                    in
                    Return.singleton
                        { model
                            | tool = SelectionTool initialSelection
                            , boxes =
                                model.boxes
                                    -- only add box if it is at least 1 x 1 pixels
                                    ++ (if newBox.width > 0 && newBox.height > 0 then
                                            [ newBox ]

                                        else
                                            []
                                       )
                        }

                SelectionTool selection ->
                    Return.singleton { model | tool = SelectionTool { selection | mouseDownEvent = Nothing } }

                _ ->
                    Return.singleton model

        KeyPress key ->
            Return.singleton
                (case String.toLower key of
                    "b" ->
                        { model | tool = BoxTool Nothing }

                    "escape" ->
                        { model | tool = SelectionTool initialSelection }

                    _ ->
                        model
                )

        NoOp ->
            Return.singleton model



-- view


sidebarWidth : Int
sidebarWidth =
    300


sidebar : Element Msg
sidebar =
    column
        [ Background.color Palette.gray0
        , Element.width (sidebarWidth |> px)
        , Element.height Element.fill
        , Element.padding Palette.mediumSpacing
        , Border.solid
        , Border.color Palette.gray100
        , Border.widthEach { top = 0, left = 1, bottom = 0, right = 0 }
        ]
        [ column
            [ Element.width Element.fill
            , Element.spacing Palette.smallSpacing
            ]
            [ el
                ([ Element.paddingEach { top = 0, right = 0, bottom = Palette.smallSpacing, left = 0 }
                 ]
                    ++ Palette.h1
                )
                (Element.text "Tools")
            , column
                [ Element.paddingEach { top = 0, right = 0, bottom = 0, left = Palette.smallSpacing }
                , Element.width Element.fill
                , Element.spacing Palette.smallSpacing
                ]
                [ toolOption "Box" "B"
                , toolOption "Frame" "F"
                ]
            ]
        ]


toolOption : String -> String -> Element.Element Msg
toolOption name shortcut =
    row
        ([ Element.width Element.fill
         , Element.spaceEvenly
         , Font.color Palette.gray700
         ]
            ++ Palette.h2
        )
        [ Element.text name
        , Element.text shortcut
        ]


canvas : List Box -> Tool -> Element.Element Msg
canvas boxes tool =
    el
        [ Element.width Element.fill
        , Element.height Element.fill
        , Background.color Palette.gray50
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
                , on "mousedown" (Decode.map MouseDown mouseEvent)
                , on "mousemove" (Decode.map MouseMove mouseEvent)
                , on "mouseup" (Decode.map MouseUp mouseEvent)

                -- TODO: handle this better
                , on "mouseleave" (Decode.map MouseUp mouseEvent)
                ]
                (List.indexedMap (boxView tool) boxes
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


mouseEvent : Decoder MouseEvent
mouseEvent =
    Decode.map3 MouseEvent
        (Decode.map2
            Point
            (Decode.map (\x -> x - sidebarWidth) (Decode.field "clientX" Decode.int))
            (Decode.field "clientY" Decode.int)
        )
        (Decode.map2
            Point
            (Decode.field "offsetX" Decode.int)
            (Decode.field "offsetY" Decode.int)
        )
        (Decode.map (Maybe.andThen String.toInt) (Decode.at [ "target", "id" ] (Decode.maybe Decode.string)))


boxView : Tool -> Int -> Box -> Html Msg
boxView tool index { top, left, width, height } =
    let
        isSelected =
            case tool of
                SelectionTool { selectedIndex } ->
                    selectedIndex == Just index

                _ ->
                    False
    in
    div
        [ id (String.fromInt index)
        , class "box"
        , classList [ ( "box__isSelected", isSelected ) ]
        , style "top" (String.fromInt top ++ "px")
        , style "left" (String.fromInt left ++ "px")
        , style "width" (String.fromInt width ++ "px")
        , style "height" (String.fromInt height ++ "px")
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
