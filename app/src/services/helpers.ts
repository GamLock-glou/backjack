export interface FetchBaseQueryError {
  status: number;
  data: {
    message: string;
  };
}

export function isFetchBaseQueryError(
  error: unknown
): error is FetchBaseQueryError {
  return typeof error === 'object' && typeof (error as any).data == 'object' && error != null && 'status' in error
}