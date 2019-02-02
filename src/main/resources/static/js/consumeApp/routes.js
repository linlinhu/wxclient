var routes = [
  // Index page
  {
    path: '/',
    url: '/customer/index',
    name: 'home',
  },
  {
    path: '/myLotteries/:ecmId/:openId/',
    componentUrl: '/customer/myLotteries',
  },
  {
    path: '/myCoupons/:ecmId/:openId/',
    componentUrl: '/customer/myCoupons',
  },
  {
    path: '/myExchangeds/:ecmId/:openId/',
    componentUrl: '/customer/myExchangeds',
  },
  {
    path: '/awardDetail/:ecmId/:id/',
    url: '/customer/awardDetail',
  }
];
