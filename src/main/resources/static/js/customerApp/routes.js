var routes = [
  // Index page
  {
    path: '/customer/index/',
    url: '/customer/index',
    name: 'home',
  },
  {
    path: '/myLotteries/:ecmId/:openId/:woaId/',
    componentUrl: '/customer/myLotteries',
  },
  {
    path: '/myCoupons/:ecmId/:openId/:woaId/',
    componentUrl: '/customer/myCoupons',
  },
  {
    path: '/myExchangeds/:ecmId/:openId/:woaId/',
    componentUrl: '/customer/myExchangeds',
  },
  {
    path: '/awardDetail/:ecmId/:id/:woaId/',
    url: '/customer/awardDetail',
  }
];
