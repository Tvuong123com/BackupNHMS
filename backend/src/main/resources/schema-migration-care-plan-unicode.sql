-- Preserve existing data and use Unicode for new/reviewed care-plan content.
IF OBJECT_ID(N'dbo.care_goals', N'U') IS NOT NULL
BEGIN
    ALTER TABLE dbo.care_goals ALTER COLUMN title NVARCHAR(255) NULL;
    ALTER TABLE dbo.care_goals ALTER COLUMN description NVARCHAR(MAX) NULL;
END;
GO
IF OBJECT_ID(N'dbo.care_interventions', N'U') IS NOT NULL
    ALTER TABLE dbo.care_interventions ALTER COLUMN title NVARCHAR(2000) NULL;
GO
IF OBJECT_ID(N'dbo.assessment_details', N'U') IS NOT NULL
    ALTER TABLE dbo.assessment_details ALTER COLUMN notes NVARCHAR(MAX) NULL;
GO
-- Repair only the known application-generated fall-prevention labels, using their exact original source text.
-- Arbitrary resident notes with lost '?' characters are not guessed or rewritten.
IF OBJECT_ID(N'dbo.care_goals', N'U') IS NOT NULL
BEGIN
    UPDATE dbo.care_goals SET title = N'Phòng ngừa Té ngã & An toàn Vận động (Fall Prevention & Mobility Safety)'
    WHERE title = CONVERT(VARCHAR(255), N'Phòng ngừa Té ngã & An toàn Vận động (Fall Prevention & Mobility Safety)');
    UPDATE dbo.care_goals SET description = N'Kế hoạch can thiệp chủ động dựa trên đánh giá phân tầng rủi ro DFRS'
    WHERE description = CONVERT(VARCHAR(255), N'Kế hoạch can thiệp chủ động dựa trên đánh giá phân tầng rủi ro DFRS');
END;
GO
IF OBJECT_ID(N'dbo.care_interventions', N'U') IS NOT NULL
    UPDATE dbo.care_interventions SET title = N'Duy trì chiếu sáng đèn ngủ ban đêm và dọn sạch chướng ngại vật trên sàn phòng'
    WHERE title = CONVERT(VARCHAR(255), N'Duy trì chiếu sáng đèn ngủ ban đêm và dọn sạch chướng ngại vật trên sàn phòng');
GO
