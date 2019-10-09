module Main exposing (main)

import Array exposing (Array)
import Browser
import Browser.Events as Events
import Canvas exposing (Box(..), Path, Point, Rectangle, RectangleDefinition)
import Element exposing (Color, Element, column, el, px, row)
import Element.Background as Background
import Element.Border as Border
import Element.Font as Font
import Html exposing (Html, div)
import Html.Attributes exposing (class, classList, style)
import Html.Events exposing (on)
import Json.Decode as Decode exposing (Decoder)
import Json.Encode as Encode
import Palette
import Return



-- model


type Tool
    = SelectionTool Selection
    | BoxTool (Maybe RectangleDefinition)


type alias Selection =
    { selectedPath : Maybe Path, mouseDownEvent : Maybe MouseEvent }


initialSelection : Selection
initialSelection =
    { selectedPath = Nothing, mouseDownEvent = Nothing }


type alias Model =
    { tool : Tool
    , boxes : Array Box
    }


type alias MouseEvent =
    { position : Point
    , positionOnTarget : Point
    , target : Maybe Path
    }


initialState : Model
initialState =
    { tool = SelectionTool initialSelection
    , boxes = Array.empty
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
                                    { selectedPath = target
                                    , mouseDownEvent = Just event
                                    }
                        }

        MouseMove { position, target } ->
            case model.tool of
                BoxTool (Just boxDefinition) ->
                    Return.singleton { model | tool = BoxTool (Just { boxDefinition | endPoint = position }) }

                SelectionTool { selectedPath, mouseDownEvent } ->
                    case ( selectedPath, mouseDownEvent ) of
                        ( Just path, Just event ) ->
                            if selectedPath == event.target then
                                Return.singleton
                                    { model
                                        | boxes =
                                            Canvas.updateBox
                                                path
                                                (\(Box box) ->
                                                    let
                                                        rectangle =
                                                            box.rectangle

                                                        movedRectangle =
                                                            { rectangle
                                                                | left = position.x - event.positionOnTarget.x
                                                                , top = position.y - event.positionOnTarget.y
                                                            }
                                                    in
                                                    Box { box | rectangle = movedRectangle }
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
                BoxTool (Just rectangleDefinition) ->
                    let
                        (Box newBox) =
                            rectangleDefinition
                                |> Canvas.rectangleFromDefinition
                                |> Canvas.boxFromRectangle
                    in
                    Return.singleton
                        { model
                            | tool = SelectionTool initialSelection
                            , boxes =
                                if newBox.rectangle.width > 0 && newBox.rectangle.height > 0 then
                                    Canvas.addBox [] (Box newBox) model.boxes

                                else
                                    model.boxes
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
                [ toolOption "Box" "B" True
                , toolOption "Stack" "S" False
                ]
            ]
        ]


toolOption : String -> String -> Bool -> Element.Element Msg
toolOption name shortcut isImplemented =
    row
        ([ Element.width Element.fill
         , Element.spaceEvenly
         , Font.color
            (if isImplemented then
                Palette.gray700

             else
                Palette.gray300
            )
         ]
            ++ Palette.h2
        )
        [ Element.text name
        , Element.text shortcut
        ]


canvasView : Array Box -> Tool -> Element.Element Msg
canvasView boxes tool =
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
                ((boxes
                    |> Array.toIndexedList
                    |> List.map (boxView tool [])
                 )
                    ++ toolPreview tool
                )
            )
        )


toolPreview : Tool -> List (Html msg)
toolPreview tool =
    case tool of
        BoxTool (Just rectangleDefinition) ->
            let
                rectangle =
                    Canvas.rectangleFromDefinition rectangleDefinition
            in
            [ div
                [ class "createBoxPreview"
                , style "width" (String.fromInt rectangle.width ++ "px")
                , style "height" (String.fromInt rectangle.height ++ "px")
                , style "top" (String.fromInt rectangle.top ++ "px")
                , style "left" (String.fromInt rectangle.left ++ "px")
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
        (Decode.at [ "target", "id" ] (Decode.maybe (Decode.list Decode.int)))


boxView : Tool -> Path -> ( Int, Box ) -> Html Msg
boxView tool path ( index, Box { rectangle, fill, children } ) =
    let
        fullPath =
            path ++ [ index ]

        isSelected =
            case tool of
                SelectionTool { selectedPath } ->
                    selectedPath == Just fullPath

                _ ->
                    False
    in
    div
        [ Html.Attributes.id (Encode.encode 0 (Encode.list Encode.int fullPath))
        , class "box"
        , classList [ ( "box__isSelected", isSelected ) ]
        , style "top" (String.fromInt rectangle.top ++ "px")
        , style "left" (String.fromInt rectangle.left ++ "px")
        , style "width" (String.fromInt rectangle.width ++ "px")
        , style "height" (String.fromInt rectangle.height ++ "px")
        , style "background-color" (Palette.colorToString fill)
        ]
        [ div
            [ class "box_inner"
            ]
            (children
                |> Array.toIndexedList
                |> List.map (boxView tool fullPath)
            )
        ]


view : Model -> Html Msg
view { boxes, tool } =
    Element.layout
        []
        (row
            [ Element.width Element.fill
            , Element.height Element.fill
            ]
            [ sidebar
            , canvasView boxes tool
            ]
        )


main : Program () Model Msg
main =
    Browser.element
        { init = init
        , update = update
        , view = view
        , subscriptions = subscriptions
        }
