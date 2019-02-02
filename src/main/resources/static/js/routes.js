var routes = [
  // Index page
  {
    path: '/',
    url: './index.html',
    name: 'home',
  },

  // Components
  {
    path: '/scanResult/',
    url: '/scanResult/index',
  },
  // Default route (404 page). MUST BE THE LAST
  {
    path: '(.*)',
    url: './pages/404.html',
  },
];
