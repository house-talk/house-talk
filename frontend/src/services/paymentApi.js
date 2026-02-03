import axios from "axios";



/**
 * 납부 기간 목록 조회 (페이지네이션 대응)
 */
export const fetchPaymentPeriods = async (
  buildingId,
  page = 0,
  size = 10,
  keyword = ""
) => {
  const res = await axios.get(
    `/api/admin/buildings/${buildingId}/payments`,
    {
      params: {
        page,
        size,
        keyword,
      },
      withCredentials: true,
    }
  );
  return res.data; // Page<PaymentPeriodResponse>
};

/**
 * 납부 기간 생성
 */
export const createPaymentPeriod = async (buildingId, body) => {
  const res = await axios.post(
    `/api/admin/buildings/${buildingId}/payments`,
    body,
    {
      withCredentials: true,
    }
  );
  return res.data;
};

/**
 * 특정 납부 기간의 세대별 납부 상태 조회
 */
export const fetchPaymentStatuses = async (paymentPeriodId) => {
  const res = await axios.get(
    `/api/admin/payments/periods/${paymentPeriodId}/statuses`,
    {
      withCredentials: true,
    }
  );
  return res.data; // List<PaymentStatusResponse>
};
