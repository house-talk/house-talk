export default function AboutPage() {
  return (
    <div style={styles.container}>
      {/* ===== Hero ===== */}
      <section style={styles.hero}>
        <h1 style={styles.title}>HOUSE-TALK</h1>
        <p style={styles.subtitle}>
          자취방 관리를 더 단순하게 만드는 서비스
        </p>
      </section>

      {/* ===== Intro ===== */}
      <section style={styles.section}>
        <h2 style={styles.sectionTitle}>HOUSE-TALK이란?</h2>
        <p style={styles.text}>
          HOUSE-TALK은 자취방 운영 과정에서 발생하는 공지, 납부 관리,
          세입자 관리를 하나의 서비스로 통합하기 위해 만들어진
          자취방 관리 플랫폼입니다.
        </p>
      </section>

      {/* ===== Problem ===== */}
      <section style={styles.section}>
        <h2 style={styles.sectionTitle}>왜 이 서비스가 필요했을까요?</h2>
        <p style={styles.text}>
          자취방을 운영하거나 거주하면서 공지사항은 카카오톡,
          납부 내역은 엑셀, 세입자 관리는 메모장 등
          여러 도구를 병행해 사용하는 경우가 많습니다.
        </p>
        <p style={styles.text}>
          이러한 방식은 관리 주체와 거주자 모두에게
          정보 누락과 혼선을 발생시키며,
          관리 효율을 떨어뜨리는 원인이 됩니다.
        </p>
        <p style={styles.text}>
          HOUSE-TALK은 이러한 문제를 해결하기 위해
          분산되어 있던 관리 흐름을 하나의 시스템으로 통합하는 것을
          목표로 합니다.
        </p>
      </section>

      {/* ===== Roles ===== */}
      <section style={styles.section}>
        <h2 style={styles.sectionTitle}>누구를 위한 서비스인가요?</h2>

        <div style={styles.roleBox}>
          <h3 style={styles.roleTitle}>관리자</h3>
          <p style={styles.text}>
            건물과 세대 정보를 체계적으로 관리하고,
            공지사항과 납부 내역을 한 곳에서 관리할 수 있습니다.
            이를 통해 세입자와의 소통을 보다 명확하고 효율적으로
            진행할 수 있습니다.
          </p>
        </div>

        <div style={styles.roleBox}>
          <h3 style={styles.roleTitle}>세입자</h3>
          <p style={styles.text}>
            공지사항을 한눈에 확인할 수 있으며,
            관리자가 전달하는 주요 안내를 놓치지 않고 확인할 수 있습니다.
            향후에는 보다 다양한 기능을 통해
            관리자와의 소통을 지원할 예정입니다.
          </p>
        </div>
      </section>

      {/* ===== Closing ===== */}
      <section style={styles.section}>
        <h2 style={styles.sectionTitle}>HOUSE-TALK의 목표</h2>
        <p style={styles.text}>
          HOUSE-TALK은 자취방 관리 과정에서 발생하는
          반복적인 불편함을 줄이고,
          관리 주체와 거주자 모두에게
          더 명확하고 안정적인 관리 경험을 제공하는 것을 목표로 합니다.
        </p>
      </section>
    </div>
  );
}

/* ===== Styles ===== */
const styles = {
  container: {
    maxWidth: "900px",
    margin: "0 auto",
    padding: "80px 24px",
  },

  hero: {
    textAlign: "center",
    marginBottom: "72px",
  },

  title: {
    fontSize: "40px",
    fontWeight: 700,
    marginBottom: "12px",
  },

  subtitle: {
    fontSize: "16px",
    color: "#6b7280",
  },

  section: {
    marginBottom: "56px",
  },

  sectionTitle: {
    fontSize: "24px",
    fontWeight: 600,
    marginBottom: "16px",
  },

  text: {
    fontSize: "15px",
    lineHeight: 1.8,
    color: "#374151",
    marginBottom: "12px",
  },

  roleBox: {
    marginBottom: "24px",
    paddingLeft: "12px",
    borderLeft: "3px solid #e5e7eb",
  },

  roleTitle: {
    fontSize: "18px",
    fontWeight: 600,
    marginBottom: "8px",
  },
};
