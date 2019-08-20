import _ from 'lodash/fp';
import React from 'react';
import './App.css';
import db from './db';
import ds from 'datascript';

function App() {
  return (
    <layout-group
      data-width='500px'
      data-top='50px'
      data-center-x='window.center.x'
    >
      <RoomOffers />
      <HotelsByDistrict />
      <RoomCategories />
    </layout-group>
  );
}

function HotelsByDistrict() {
  const districts = _.map(([id, name]) => ({ id, name }), ds.q(`
    [:find ?district ?name
     :where [?district "is kind of" "district"]
            [?district "has name" ?name]]    
  `, db))


  return (
    <layout-group
      data-flow="down"
      data-represents="hotels"
      data-left="roomOffers.right"
      data-top="$this.parent.top"
      data-right="$this.parent.right"
    >
      {_.map((district) => {

        const hotels = _.map(([id, name]) => ({ id, name }), ds.q(`
          [:find ?hotel ?name
           :where [?hotel "is kind of" "hotel"]
                  [?hotel "has name" ?name]
                  [?hotel "is in district" "${district.id}"]]
        `, db))

        return (
          <layout-group
            data-flow="down"
            data-represents={district.id}
          >
            <layout-box
              drepresents='name'
              style={{ background: 'blue', color: '#fff' }}
            >
              {district.name}
            </layout-box>

            {
              _.map((hotel) => (
                <layout-box data-represents={hotel.id}>
                  {hotel.name}
                </layout-box>
              ), hotels)
            }
          </layout-group>
        )
      }, districts)}
    </layout-group>
  )
}

function RoomOffers() {
  const cateogories = _.map(([id]) => ({ id }), ds.q(`
    [:find ?category
     :where [?category "is kind of" "category"]]
  `, db))

  return (
    <layout-group
      data-represents="roomOffers"
      data-flow="right"
      data-top="0px"
      data-left="0px"
      data-bottom="$this.parent.bottom"
    >
      {_.map((category) => {
        const roomOffers = _.map(([id, price, hotelId]) => ({ id, price, hotelId }), ds.q(`
        [:find ?offer ?price ?hotel
         :where [?offer "is kind of" "room offer"]
                [?offer "has price" ?price]
                [?offer "has category" "${category.id}"]
                [?hotel "offers" ?offer]]
      `, db))

        return (
          <layout-group
            data-represents={category.id}
            data-flow="down"
          >
            {_.map((roomOffer) => (
              <layout-box
                data-represents={roomOffer.id}
                data-center-y={`hotels..${roomOffer.hotelId}.center.y`}
                style={{ textAlign: 'right' }}
              >
                $ {roomOffer.price}
              </layout-box>
            ), roomOffers)}
          </layout-group>
        );
      }, cateogories)}
    </layout-group>
  )
}

function RoomCategories() {
  const categories = _.map(([id, name, description]) => ({ id, name, description }), ds.q(`
    [:find ?category ?name ?description
    :where [?category "is kind of" "category"]
           [?category "has name" ?name]
           [?category "has description" ?description]]
  `, db))

  return (
    <layout-group
      data-represents="category"
      data-flow="down"
      data-top="hotels.bottom"
      data-right="hotels.right"
    >
      {_.map((category) => {
        return (
          <layout-group
            data-represents={category.id}
            data-flow="right"
          >
            <layout-box data-represents={`${category}.name`}>{category.name}</layout-box>
            <layout-box data-represents={`${category}.decription`}>{category.description}</layout-box>
          </layout-group>
        )
      }, categories)}
    </layout-group>
  )
}


export default App;
