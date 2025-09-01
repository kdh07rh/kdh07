import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomString, randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
  stages: [
    { duration: '5m', target: 3 },    // 5분 동안 3명까지 점진적 증가
    { duration: '10h', target: 3 },   // 10시간 동안 3명 지속 유지
    { duration: '5m', target: 0 },    // 5분 동안 0명까지 감소
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'], // 95%의 요청이 2초 이내
    http_req_failed: ['rate<0.1'],     // 실패율 10% 미만
  },
};

// Tomcat이 실행 중인 포트 (기본값 8080)
const BASE_URL = 'http://localhost:8080/shopping-mall';

// 테스트에 사용할 사용자 데이터
const TEST_USERS = [
  { username: 'testuser1', password: 'password123', email: 'test1@example.com' },
  { username: 'testuser2', password: 'password123', email: 'test2@example.com' },
  { username: 'testuser3', password: 'password123', email: 'test3@example.com' }
];

export default function () {
  const user = TEST_USERS[Math.floor(Math.random() * TEST_USERS.length)];
  
  // 1. 회원가입 페이지 (register.jsp) 테스트
  let response = http.get(`${BASE_URL}/register.jsp`);
  check(response, {
    'register page loaded': (r) => r.status === 200,
    'register page contains form': (r) => r.body.includes('form'),
  });
  sleep(randomIntBetween(2, 4));

  // 2. 로그인 페이지 (login.jsp) 테스트
  response = http.get(`${BASE_URL}/login.jsp`);
  check(response, {
    'login page loaded': (r) => r.status === 200,
    'login page contains form': (r) => r.body.includes('form'),
  });
  sleep(randomIntBetween(2, 4));

  // 3. 로그인 시도
  response = http.post(`${BASE_URL}/login`, {
    username: user.username,
    password: user.password,
  });
  
  // 로그인 성공 여부에 관계없이 계속 진행
  const cookies = response.cookies;
  const headers = cookies ? { 'Cookie': Object.keys(cookies).map(key => `${key}=${cookies[key][0].value}`).join('; ') } : {};

  // 4. 상품 목록 페이지 (products.jsp) 테스트
  response = http.get(`${BASE_URL}/products.jsp`, { headers });
  check(response, {
    'products page loaded': (r) => r.status === 200,
    'products page has content': (r) => r.body.length > 0,
  });
  sleep(randomIntBetween(2, 4));

  // 5. 상품 상세 페이지 (product-details.jsp) 테스트
  // 임의의 상품 ID로 테스트
  const productId = randomIntBetween(1, 10);
  response = http.get(`${BASE_URL}/product-details.jsp?id=${productId}`, { headers });
  check(response, {
    'product details page loaded': (r) => r.status === 200 || r.status === 404,
    'product details page has content': (r) => r.body.length > 0,
  });
  sleep(randomIntBetween(2, 4));

  // 6. 장바구니 페이지 (cart.jsp) 테스트
  response = http.get(`${BASE_URL}/cart.jsp`, { headers });
  check(response, {
    'cart page loaded': (r) => r.status === 200,
    'cart page has content': (r) => r.body.length > 0,
  });
  sleep(randomIntBetween(2, 4));

  // 7. 프로필 페이지 (profile.jsp) 테스트
  response = http.get(`${BASE_URL}/profile.jsp`, { headers });
  check(response, {
    'profile page loaded': (r) => r.status === 200 || r.status === 302, // 로그인 안된 경우 리다이렉트
    'profile page response': (r) => r.body.length > 0,
  });
  sleep(randomIntBetween(2, 4));

  // 장바구니에 상품 추가 테스트 (선택적)
  if (Math.random() > 0.5) {
    response = http.post(`${BASE_URL}/cart`, {
      productId: productId,
      quantity: randomIntBetween(1, 3),
    }, { headers });
    
    check(response, {
      'add to cart response': (r) => r.status >= 200 && r.status < 400,
    });
  }

  // 10시간 지속 테스트를 위해 요청 간격을 넉넉하게 설정
  sleep(randomIntBetween(5, 15));
}

// 테스트 시작 시 실행되는 setup 함수
export function setup() {
  console.log('Starting 10-hour sustained load test for shopping mall JSP pages');
  console.log(`Base URL: ${BASE_URL}`);
  console.log('Test configuration: 3 concurrent users, 10-hour duration, moderate load');
  
  // 기본 연결 테스트
  const response = http.get(`${BASE_URL}/products.jsp`);
  if (response.status !== 200) {
    console.error(`Setup failed: Unable to reach ${BASE_URL}/products.jsp (Status: ${response.status})`);
    console.error('Please ensure Tomcat is running and the application is deployed');
  } else {
    console.log('Setup successful: Application is accessible');
    console.log('WARNING: This test will run for 10 hours. Make sure system resources are adequate.');
  }
  
  return { baseUrl: BASE_URL };
}

// 테스트 종료 시 실행되는 teardown 함수
export function teardown(data) {
  console.log('10-hour sustained load test completed');
  console.log('Test summary: 3 concurrent users over 10 hours with moderate request intervals');
}