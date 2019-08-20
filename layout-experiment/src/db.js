import ds from 'datascript';
import _ from 'lodash/fp';

const facts =
    _.flatten([
        // CATEGORIES
        getCategory('$single', {
            name: 'single bed',
            description: 'just a single bed for one person',
        }),
        getCategory('$double', {
            name: 'double bed',
            description: 'one double bed for up to two people',
        }),
        getCategory('$honeyMoonSuite', {
            name: 'Honey Moon suite',
            description: 'for a very special occasion',
        }),
        getCategory('$presidentSuite', {
            name: 'President Suite',
            description: 'if you just don\'t care about money',
        }),

        // DISTRICTS
        getDistrictWithRandomHotels('$downtown', { name: 'Downtown' }),
        getDistrictWithRandomHotels('$eastSide', { name: 'East Side' }),
        getDistrictWithRandomHotels('$westSide', { name: 'West Side' }),
    ])

const db = ds.init_db(facts)

function getCategory(category, { name, description }) {
    return [
        [category, 'is kind of', 'category'],
        [category, 'has name', name],
        [category, 'has description', description]
    ]
}

function getDistrictWithRandomHotels(district, { name }) {
    return [
        [district, 'is kind of', 'district'],
        [district, 'has name', name],
    ].concat(
        getRandomHotels({ district, numberOfHotels: _.random(3, 5) })
    )
}

function getRandomHotels({ district, numberOfHotels }) {
    return _.flatten(
        _.times(() => {
            const hotel = ds.squuid();

            const basePrice = _.random(50, 300)
            const factor = _.random(1.2, 1.5)

            return ([
                [hotel, 'has name', randomHotelName(basePrice)],
                [hotel, 'is kind of', 'hotel'],
                [hotel, 'is in district', district],
            ]
                .concat(
                    getRoomOffer({ hotel, category: '$single', price: basePrice })
                )
                .concat(
                    getRoomOffer({ hotel, category: '$double', price: Math.round(basePrice * factor) }),
                )
                .concat(
                    (basePrice > 100) ?
                        getRoomOffer({ hotel, category: '$honeyMoonSuite', price: Math.round(basePrice * Math.pow(factor, 2)) }) : []
                )
                .concat(
                    (basePrice > 200) ?
                        getRoomOffer({ hotel, category: '$presidenSuite', price: Math.round(basePrice * Math.pow(factor, 3)) }) : []
                )
            )
        }, numberOfHotels)
    )
}

function randomHotelName(basePrice) {
    const adjectives = ['North', 'South', 'West', 'East', 'Royal', 'Best', 'Blue', 'Golden'];
    const things = ['Castle', 'Garden', 'Ocean', 'Village'];
    const kinds = basePrice > 100 ? ['Hotel', 'Resort'] : ['Hostel', 'Motel'];

    return [
        adjectives[_.random(0, adjectives.length - 1)],
        things[_.random(0, things.length - 1)],
        kinds[_.random(0, kinds.length - 1)]
    ].join(' ');
}

function getRoomOffer({ hotel, category, price }) {
    const roomOffer = ds.squuid()

    return [
        [hotel, 'offers', roomOffer],
        [roomOffer, 'is kind of', 'room offer'],
        [roomOffer, 'has category', category],
        [roomOffer, 'has price', price]
    ]
}

export default db;