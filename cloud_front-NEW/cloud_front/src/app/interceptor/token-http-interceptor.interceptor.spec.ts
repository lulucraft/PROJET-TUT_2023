import { TestBed } from '@angular/core/testing';

import { TokenHttpInterceptorInterceptor } from './token-http-interceptor.interceptor';

describe('TokenHttpInterceptorInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      TokenHttpInterceptorInterceptor
      ]
  }));

  it('should be created', () => {
    const interceptor: TokenHttpInterceptorInterceptor = TestBed.inject(TokenHttpInterceptorInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
